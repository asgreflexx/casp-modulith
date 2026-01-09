package casp.web.backend.member.data;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MemberCustomRepositoryImpl.class);
    private static final QMember MEMBER = QMember.member;
    private static final BooleanExpression ACTIVE_MEMBER_STATUS_FILTER = MEMBER.entityStatus.eq(EntityStatus.ACTIVE);
    private static final String SPLIT_WORDS_WITH_SPACE = " ";
    private final MongoOperations mongoOperations;

    @Autowired
    MemberCustomRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Page<Member> findAllByEntityStatusNameAndRoles(EntityStatus entityStatus, String name, final Set<Role> roles, Pageable pageable) {
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
            LOG.error(msg);
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
    public MembershipFeesStats getMembershipFeesStats() {
        var year = LocalDate.now().getYear();
        var thisYear = sumMembershipFeesByYear(year);
        var lastYear = sumMembershipFeesByYear(year - 1);
        var twoYearsAgo = sumMembershipFeesByYear(year - 2);
        return new MembershipFeesStats(thisYear, lastYear, twoYearsAgo);
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

    private static double calculateTotalPaidFeesForYear(int year, Set<MembershipFee> msf) {
        return msf
                .stream()
                .filter(m -> m.getPaidDate().getYear() == year)
                .mapToDouble(MembershipFee::getPaidPrice).sum();
    }

    private SpringDataMongodbQuery<Member> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Member.class);
    }

    private MembershipFeesStatsByYear sumMembershipFeesByYear(int year) {
        var paymentRange = MEMBER.membershipFees.any().paidDate.between(LocalDate.of(year, 1, 1),
                LocalDate.of(year, 12, 31));
        double totalPaid = createQuery()
                .where(ACTIVE_MEMBER_STATUS_FILTER, paymentRange)
                .stream()
                .map(member -> calculateTotalPaidFeesForYear(year, member.getMembershipFees()))
                .reduce(0.0, Double::sum);
        return new MembershipFeesStatsByYear(year, totalPaid);
    }
}
