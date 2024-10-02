package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Set;
import java.util.UUID;

public interface SpaceRepository extends MongoRepository<Space, UUID>, QuerydslPredicateExecutor<Space>, SpaceCustomRepository, BaseParticipantCustomRepository {
    Set<Space> findAllByBaseEventIdAndEntityStatus(UUID courseId, EntityStatus entityStatus);

    Set<Space> findAllByBaseEventIdAndEntityStatusNot(UUID courseId, EntityStatus entityStatus);

    void deleteAllByBaseEventId(UUID courseId);
}
