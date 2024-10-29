package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

abstract class BaseEventCustomRepositoryImpl<T extends BaseEvent> implements BaseEventCustomRepository<T> {
    private static final QBaseEvent BASE_EVENT = QBaseEvent.baseEvent;
    final MongoOperations mongoOperations;
    private final Class<T> baseEventClass;

    // no need to check, all classes are of type BaseEvent
    @SuppressWarnings("unchecked")
    BaseEventCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.baseEventClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<T> findAllByMemberIdAndNotDeleted(final UUID memberId) {
        var criteria = BASE_EVENT.entityStatus.ne(EntityStatus.DELETED)
                .and(BASE_EVENT.member.id.eq(memberId));
        return findAllByCriteria(criteria);
    }

    @Override
    public Set<T> findAllByMemberIdAndStatus(final UUID memberId, final EntityStatus status) {
        var criteria = BASE_EVENT.entityStatus.eq(status)
                .and(BASE_EVENT.member.id.eq(memberId));
        return findAllByCriteria(criteria);
    }

    @Override
    public Set<T> findAllBetweenFromAndTo(final LocalDateTime from, final LocalDateTime to) {
        var criteria = BASE_EVENT.entityStatus.eq(EntityStatus.ACTIVE)
                .and(BASE_EVENT.minTime.loe(to))
                .and(BASE_EVENT.maxTime.goe(from));
        return findAllByCriteria(criteria);
    }

    private Set<T> findAllByCriteria(final BooleanExpression criteria) {
        return query()
                .where(criteria)
                .stream()
                .collect(Collectors.toSet());
    }

    SpringDataMongodbQuery<T> query() {
        return new SpringDataMongodbQuery<>(mongoOperations, baseEventClass);
    }
}
