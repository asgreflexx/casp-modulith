package casp.web.backend.calendar.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.QDogHasHandlerReference;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

abstract class BaseEventCustomRepositoryImpl<T extends BaseEvent<?>> implements BaseEventCustomRepository<T> {
    private static final QBaseEvent BASE_EVENT = QBaseEvent.baseEvent;
    final MongoOperations mongoOperations;
    private final Class<T> baseEventClass;

    // no need to check, all classes are of type BaseEvent
    @SuppressWarnings("unchecked")
    BaseEventCustomRepositoryImpl(MongoOperations mongoOperations) {
        baseEventClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<T> findAllByMemberIdAndNotDeleted(UUID memberId) {
        var criteria = BASE_EVENT.entityStatus.ne(EntityStatus.DELETED)
                .and(BASE_EVENT.member.id.eq(memberId));
        return findAllByCriteria(criteria);
    }

    @Override
    public Set<T> findAllByMemberIdAndStatus(UUID memberId, EntityStatus status) {
        var criteria = BASE_EVENT.entityStatus.eq(status)
                .and(BASE_EVENT.member.id.eq(memberId));
        return findAllByCriteria(criteria);
    }

    protected static BooleanExpression createTimeRangeCriteria(LocalDateTime from, LocalDateTime to) {
        return BASE_EVENT.entityStatus.eq(EntityStatus.ACTIVE)
                .and(BASE_EVENT.maxTime.goe(from)
                        .and(BASE_EVENT.minTime.loe(to)));
    }

    protected SpringDataMongodbQuery<T> query() {
        return new SpringDataMongodbQuery<>(mongoOperations, baseEventClass);
    }

    protected List<UUID> findDogHasHandlerIdsByMemberId(UUID memberId) {
        var dogHasHandlerReference = QDogHasHandlerReference.dogHasHandlerReference;
        var dogHasHandlerReferenceQuery = new SpringDataMongodbQuery<>(mongoOperations, DogHasHandlerReference.class);
        return dogHasHandlerReferenceQuery.where(dogHasHandlerReference.entityStatus.eq(EntityStatus.ACTIVE)
                        .and(dogHasHandlerReference.member.id.eq(memberId)))
                .stream()
                .map(DogHasHandlerReference::getId)
                .toList();
    }

    private Set<T> findAllByCriteria(BooleanExpression criteria) {
        return query()
                .where(criteria)
                .stream()
                .collect(Collectors.toSet());
    }
}
