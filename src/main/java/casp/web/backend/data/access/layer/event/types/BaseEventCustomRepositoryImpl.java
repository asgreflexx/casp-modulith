package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

abstract class BaseEventCustomRepositoryImpl<T extends BaseEvent> implements BaseEventCustomRepository<T> {
    private static final QBaseEvent BASE_EVENT = QBaseEvent.baseEvent;
    final MongoOperations mongoOperations;
    private final Class<T> baseEventClass;

    BaseEventCustomRepositoryImpl(final Class<T> baseEventClass, final MongoOperations mongoOperations) {
        this.baseEventClass = baseEventClass;
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
