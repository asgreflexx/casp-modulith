package casp.web.backend.dog.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.QDogReference;
import casp.web.backend.common.reference.QMemberReference;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class DogHasHandlerCustomRepositoryImpl implements DogHasHandlerCustomRepository {
    private static final QDogHasHandler DOG_HAS_HANDLER = QDogHasHandler.dogHasHandler;
    private final MongoOperations mongoOperations;

    @Autowired
    DogHasHandlerCustomRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<DogHasHandler> findAllByDogIdAndNotDeleted(UUID dogId) {
        var expression = DOG_HAS_HANDLER.dog.id.eq(dogId).and(DOG_HAS_HANDLER.entityStatus.ne(EntityStatus.DELETED));
        return executeQuery(expression);
    }

    @Override
    public Set<DogHasHandler> findAllByMemberIdAndNotDeleted(UUID memberId) {
        var expression = DOG_HAS_HANDLER.member.id.eq(memberId).and(DOG_HAS_HANDLER.entityStatus.ne(EntityStatus.DELETED));
        return executeQuery(expression);
    }

    @Override
    public Set<DogHasHandler> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus) {
        var expression = DOG_HAS_HANDLER.member.id.eq(memberId).and(DOG_HAS_HANDLER.entityStatus.eq(entityStatus));
        return executeQuery(expression);
    }

    @Override
    public Set<DogHasHandler> findAllByDogIdAndEntityStatus(UUID dogId, EntityStatus entityStatus) {
        var expression = DOG_HAS_HANDLER.dog.id.eq(dogId).and(DOG_HAS_HANDLER.entityStatus.eq(entityStatus));
        return executeQuery(expression);
    }

    @Override
    public Page<DogHasHandler> findAllByValue(@Nullable String value, Pageable pageable) {
        var expression = DOG_HAS_HANDLER.entityStatus.eq(EntityStatus.ACTIVE);
        if (StringUtils.isNotBlank(value)) {
            expression = expression.and(DOG_HAS_HANDLER.dog.id.in(findAllByDogName(value, pageable))
                    .or(DOG_HAS_HANDLER.member.id.in(findAllByMemberName(value, pageable))));
        }
        return query()
                .where(expression)
                .fetchPage(pageable);
    }

    @Override
    public Optional<DogHasHandler> findByDogIdAndMemberId(UUID dogId, UUID memberId) {
        var expression = DOG_HAS_HANDLER.entityStatus.eq(EntityStatus.ACTIVE)
                .and(DOG_HAS_HANDLER.dog.id.eq(dogId))
                .and(DOG_HAS_HANDLER.member.id.eq(memberId));
        return query()
                .where(expression)
                .stream()
                .findAny();
    }

    private Set<UUID> findAllByDogName(String value, Pageable pageable) {
        var query = new SpringDataMongodbQuery<>(mongoOperations, DogReference.class);
        var dog = QDogReference.dogReference;
        var expression = dog.entityStatus.eq(EntityStatus.ACTIVE)
                .and(dog.name.containsIgnoreCase(value));
        return query
                .where(expression)
                .fetchPage(pageable)
                .stream()
                .map(DogReference::getId)
                .collect(Collectors.toSet());
    }

    private Set<UUID> findAllByMemberName(String name, Pageable pageable) {
        var query = new SpringDataMongodbQuery<>(mongoOperations, MemberReference.class);
        var member = QMemberReference.memberReference;
        var expression = member.entityStatus.eq(EntityStatus.ACTIVE)
                .and(member.firstName.containsIgnoreCase(name)
                        .or(member.lastName.containsIgnoreCase(name)));
        return query
                .where(expression)
                .fetchPage(pageable)
                .stream()
                .map(MemberReference::getId)
                .collect(Collectors.toSet());
    }

    private Set<DogHasHandler> executeQuery(BooleanExpression expression) {
        return query()
                .where(expression)
                .stream()
                .collect(Collectors.toSet());
    }

    private SpringDataMongodbQuery<DogHasHandler> query() {
        return new SpringDataMongodbQuery<>(mongoOperations, DogHasHandler.class);
    }
}
