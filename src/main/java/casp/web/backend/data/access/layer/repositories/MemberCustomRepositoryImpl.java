package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.data.access.layer.documents.member.QMember;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
class MemberCustomRepositoryImpl implements MemberCustomRepository {
    public static final QMember MEMBER = QMember.member;
    private static final OrderSpecifier<?>[] DEFAULT_ORDER = {MEMBER.lastName.asc(), MEMBER.firstName.asc()};
    private static final String SPLIT_WORDS_WITH_SPACE = " ";
    private final MongoOperations mongoOperations;

    @Autowired
    MemberCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    private static BooleanExpression[] splitIntoWords(final String name) {
        return Arrays.stream(name.trim().split(SPLIT_WORDS_WITH_SPACE))
                .map(String::trim)
                .filter(ObjectUtils::isNotEmpty)
                .map(MemberCustomRepositoryImpl::createFullTextExpression)
                .toArray(BooleanExpression[]::new);
    }

    private static BooleanExpression createFullTextExpression(final String word) {
        return MEMBER.firstName.containsIgnoreCase(word).or(MEMBER.lastName.containsIgnoreCase(word));
    }

    @Override
    public List<Member> findAllByFirstNameAndLastName(final String firstName, final String lastName) {
        var expression = MEMBER.entityStatus.eq(EntityStatus.ACTIVE);
        if (ObjectUtils.isNotEmpty(firstName)) {
            expression = expression.and(MEMBER.firstName.equalsIgnoreCase(firstName));
        }
        if (ObjectUtils.isNotEmpty(lastName)) {
            expression = expression.and(MEMBER.lastName.equalsIgnoreCase(lastName));
        }
        return createQuery().where(expression)
                .orderBy(DEFAULT_ORDER)
                .fetch();
    }

    @Override
    public Page<Member> findAllByValue(final String value, final Pageable pageable) {
        var expression = MEMBER.entityStatus.eq(EntityStatus.ACTIVE);
        if (ObjectUtils.isNotEmpty(value)) {
            expression = expression.andAnyOf(splitIntoWords(value));
        }
        return createQuery().where(expression)
                .fetchPage(pageable);
    }

    private SpringDataMongodbQuery<Member> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Member.class);
    }
}
