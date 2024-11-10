package casp.web.backend.data.access.layer.member;

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

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Component
class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MemberCustomRepositoryImpl.class);
    private static final QMember MEMBER = QMember.member;
    private static final String SPLIT_WORDS_WITH_SPACE = " ";
    private final MongoOperations mongoOperations;

    @Autowired
    MemberCustomRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
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
    public Page<Member> findAllByValue(String value, Pageable pageable) {
        var expression = MEMBER.entityStatus.eq(EntityStatus.ACTIVE);
        if (ObjectUtils.isNotEmpty(value)) {
            expression = expression.andAnyOf(splitIntoWords(value));
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

    private SpringDataMongodbQuery<Member> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Member.class);
    }
}
