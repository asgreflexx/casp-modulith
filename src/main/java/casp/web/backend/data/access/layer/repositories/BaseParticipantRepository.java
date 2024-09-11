package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.custom.repositories.BaseParticipantCustomRepository;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Set;
import java.util.UUID;

public interface BaseParticipantRepository extends MongoRepository<BaseParticipant, UUID>, QuerydslPredicateExecutor<BaseParticipant>, BaseParticipantCustomRepository {
    void deleteAllByBaseEventId(UUID baseEventId);

    Set<BaseParticipant> findAllByBaseEventIdAndEntityStatusAndParticipantType(UUID baseEventId, EntityStatus entityStatus, String participantType);

    Set<BaseParticipant> findAllByBaseEventIdAndEntityStatusNotAndParticipantType(UUID baseEventId, EntityStatus entityStatus, String participantType);
}
