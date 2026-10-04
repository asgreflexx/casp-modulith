package casp.web.backend.member.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.member.MembershipFeesStatsByYearDto;
import casp.web.backend.member.MembershipFeesStatsDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private static final QMember MEMBER = QMember.member;
    private static final BooleanExpression ACTIVE_MEMBER_STATUS_FILTER = MEMBER.entityStatus.eq(EntityStatus.ACTIVE);
    private static final QMembershipFee MEMBERSHIP_FEE = QMembershipFee.membershipFee;
    private static final String SPLIT_WORDS_WITH_SPACE = " ";
    private static final String TOTAL_PAID = "totalPaid";
    private static final String AGGREGATION_ID = "_id";
    private final MongoOperations mongoOperations;
    private final ZoneId zoneId;

    @Autowired
    MemberCustomRepositoryImpl(MongoOperations mongoOperations, ZoneId zoneId) {
        this.mongoOperations = mongoOperations;
        this.zoneId = zoneId;
    }

    @Override
    public Page<Member> findAllByEntityStatusNameAndRoles(EntityStatus entityStatus, String name, Set<Role> roles, Pageable pageable) {
        var expression = MEMBER.entityStatus.eq(entityStatus);
        if (ObjectUtils.isNotEmpty(name)) {
            expression = expression.andAnyOf(splitIntoWords(name));
        }
        if (ObjectUtils.isNotEmpty(roles)) {
            expression = expression.and(MEMBER.roles.any().in(roles));
        }
        return createQuery().where(expression)
                .fetchPage(pageable);
    }

    // fetchOne can return a null
    @SuppressWarnings("OptionalOfNullableMisuse")
    @Override
    public Member findByIdAndEntityStatusCustom(UUID id, EntityStatus entityStatus) {
        var expression = MEMBER.entityStatus.eq(entityStatus).and(MEMBER.id.eq(id));
        return Optional.ofNullable(createQuery().where(expression).fetchOne()).orElseThrow(() -> {
            var msg = "Member with id %s not found or it isn't %s.".formatted(id, entityStatus);
            log.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    @Override
    public Set<String> findAllActiveMembersEmails() {
        return createQuery()
                .where(ACTIVE_MEMBER_STATUS_FILTER)
                .fetch()
                .stream()
                .map(Member::getEmail)
                .collect(Collectors.toSet());
    }

    @Override
    public MembershipFeesStatsDto getMembershipFeesStats() {
        var thisYear = LocalDate.now(zoneId).getYear();
        var lastYear = thisYear - 1;
        var twoYearsAgo = thisYear - 2;
        var membershipFeeFieldName = MEMBER.membershipFees.getMetadata().getName();
        var paidDateFieldName = MEMBERSHIP_FEE.paidDate.getMetadata().getName();
        var paidPriceFieldName = MEMBERSHIP_FEE.paidPrice.getMetadata().getName();

        var aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(MEMBER.entityStatus.getMetadata().getName()).is(EntityStatus.ACTIVE.name())),
                Aggregation.unwind(membershipFeeFieldName),
                Aggregation.project()
                        .and("%s.%s".formatted(membershipFeeFieldName, paidDateFieldName)).extractYear().as(paidDateFieldName)
                        .and("%s.%s".formatted(membershipFeeFieldName, paidPriceFieldName)).as(paidPriceFieldName),
                Aggregation.match(Criteria.where(paidDateFieldName).in(twoYearsAgo, lastYear, thisYear)),
                Aggregation.group(paidDateFieldName).sum(paidPriceFieldName).as(TOTAL_PAID)
        );
        var membershipFeesStatsMap = mongoOperations.aggregate(aggregation, Member.class, Map.class)
                .getMappedResults()
                .stream()
                .collect(Collectors.toMap(v -> (Integer) v.get(AGGREGATION_ID),
                        v -> (Double) v.get(TOTAL_PAID)));

        var thisYearStats = generateMembershipFeesStatsByYear(thisYear, membershipFeesStatsMap);
        var lastYearStats = generateMembershipFeesStatsByYear(lastYear, membershipFeesStatsMap);
        var twoYearsAgoStats = generateMembershipFeesStatsByYear(twoYearsAgo, membershipFeesStatsMap);
        return new MembershipFeesStatsDto(thisYearStats, lastYearStats, twoYearsAgoStats);
    }

    private static BooleanExpression[] splitIntoWords(String name) {
        return Arrays.stream(name.trim().split(SPLIT_WORDS_WITH_SPACE))
                .map(String::trim)
                .filter(ObjectUtils::isNotEmpty)
                .map(MemberCustomRepositoryImpl::createFullTextExpression)
                .toArray(BooleanExpression[]::new);
    }

    private static BooleanExpression createFullTextExpression(String word) {
        return MEMBER.firstName.containsIgnoreCase(word).or(MEMBER.lastName.containsIgnoreCase(word));
    }

    private static MembershipFeesStatsByYearDto generateMembershipFeesStatsByYear(int year, Map<Integer, Double> membershipFeesStatsMap) {
        return new MembershipFeesStatsByYearDto(year, membershipFeesStatsMap.getOrDefault(year, 0.0));
    }

    private SpringDataMongodbQuery<Member> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Member.class);
    }
}
