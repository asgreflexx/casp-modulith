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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

abstract class BaseEventCustomRepositoryImpl<T extends BaseEvent<?>> implements BaseEventCustomRepository<T> {
    private static final QBaseEvent BASE_EVENT = QBaseEvent.baseEvent;
    private static final QDogHasHandlerReference DOG_HAS_HANDLER_REFERENCE = QDogHasHandlerReference.dogHasHandlerReference;
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
        var memberIdCondition = DOG_HAS_HANDLER_REFERENCE.member.id.eq(memberId);
        return activeDogHasHandlerQuery(memberIdCondition)
                .stream()
                .map(DogHasHandlerReference::getId)
                .toList();
    }

    protected Optional<UUID> findActiveDogHandlerReferenceById(UUID participantId) {
        return activeDogHasHandlerQuery(DOG_HAS_HANDLER_REFERENCE.id.eq(participantId))
                .stream()
                .findAny()
                .map(DogHasHandlerReference::getId);
    }

    private Set<T> findAllByCriteria(BooleanExpression criteria) {
        return query()
                .where(criteria)
                .stream()
                .collect(Collectors.toSet());
    }

    private SpringDataMongodbQuery<DogHasHandlerReference> dogHasHandlerQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, DogHasHandlerReference.class);
    }

    private SpringDataMongodbQuery<DogHasHandlerReference> activeDogHasHandlerQuery(BooleanExpression expression) {
        return dogHasHandlerQuery().where(DOG_HAS_HANDLER_REFERENCE.entityStatus.eq(EntityStatus.ACTIVE), expression);
    }
}
