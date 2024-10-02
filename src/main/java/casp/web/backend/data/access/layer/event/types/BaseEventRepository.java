package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BaseEventRepository extends MongoRepository<BaseEvent, UUID>, QuerydslPredicateExecutor<BaseEvent>, BaseEventCustomRepository {

    Optional<BaseEvent> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Set<BaseEvent> findAllByMemberIdAndEntityStatusNotAndEventType(UUID memberId, EntityStatus entityStatus, String eventType);

    Set<BaseEvent> findAllByMemberIdAndEntityStatusAndEventType(UUID memberId, EntityStatus entityStatus, String eventType);
}
