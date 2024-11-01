package casp.web.backend.deprecated.event.participants;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface BaseParticipantRepository extends MongoRepository<BaseParticipant, UUID>, QuerydslPredicateExecutor<BaseParticipant> {
    Set<BaseParticipant> findAllByBaseEventIdAndParticipantType(UUID baseEventId, String participantType);
}
