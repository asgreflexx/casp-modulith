package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BaseEventRepository extends MongoRepository<BaseEvent, UUID>, QuerydslPredicateExecutor<BaseEvent>, BaseEventCustomRepository {

    Optional<BaseEvent> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Optional<BaseEvent> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

    Set<BaseEvent> findAllByMemberIdAndEntityStatusNot(UUID memberId, EntityStatus entityStatus);

    Set<BaseEvent> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);
}
