package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BaseEventRepository extends MongoRepository<BaseEvent, UUID>, QuerydslPredicateExecutor<BaseEvent> {

    Optional<BaseEvent> findBaseEventByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Set<BaseEvent> findAllByMemberIdAndEntityStatusNotLike(UUID memberId, EntityStatus entityStatus);

    Set<BaseEvent> findAllByEventTypeInAndEntityStatusAndMinLocalDateTimeGreaterThanEqualAndMaxLocalDateTimeLessThanEqual(
            Set<String> eventTypes,
            EntityStatus entityStatus,
            LocalDateTime minLocalDateTime,
            LocalDateTime maxLocalDateTime
    );
}
