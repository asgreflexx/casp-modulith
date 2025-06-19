package casp.web.backend.member.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MemberCustomRepositoryImpl.class);
    private static final QMember MEMBER = QMember.member;
    private static final String SPLIT_WORDS_WITH_SPACE = " ";
    private final MongoOperations mongoOperations;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @Autowired
    MemberCustomRepositoryImpl(MongoOperations mongoOperations, DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        this.mongoOperations = mongoOperations;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
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

    @Override
    public Page<Member> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        var expression = MEMBER.entityStatus.eq(EntityStatus.ACTIVE);
        if (ObjectUtils.isNotEmpty(firstName)) {
            expression = expression.and(MEMBER.firstName.equalsIgnoreCase(firstName));
        }
        if (ObjectUtils.isNotEmpty(lastName)) {
            expression = expression.and(MEMBER.lastName.equalsIgnoreCase(lastName));
        }
        return createQuery().where(expression)
                .fetchPage(pageable);
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
                .where(MEMBER.entityStatus.eq(EntityStatus.ACTIVE))
                .fetch()
                .stream()
                .map(Member::getEmail)
                .collect(Collectors.toSet());
    }

    @Override
    public Page<Member> findAllByNotDogId(UUID dogId, String name, Pageable pageable) {
        var expression = MEMBER.entityStatus.eq(EntityStatus.ACTIVE)
                .and(MEMBER.id.notIn(getMemberIdsRelatedToThisDog(dogId)));
        if (ObjectUtils.isNotEmpty(name)) {
            expression = expression.andAnyOf(splitIntoWords(name));
        }
        return createQuery()
                .where(expression)
                .fetchPage(pageable);
    }

    private List<UUID> getMemberIdsRelatedToThisDog(UUID dogId) {
        return dogHasHandlerReferenceRepository.findAllByDogId(dogId).stream().map(dhh -> dhh.getMember().getId()).toList();
    }

    private SpringDataMongodbQuery<Member> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Member.class);
    }
}
