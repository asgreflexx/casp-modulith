package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Set;
import java.util.UUID;

public interface BaseParticipantRepository extends MongoRepository<BaseParticipant, UUID>,
        QuerydslPredicateExecutor<BaseParticipant> {
    Set<BaseParticipant> findBaseParticipantsByBaseEventAndEntityStatusAndParticipantType(BaseEvent baseEvent,
                                                                                          EntityStatus entityStatus,
                                                                                          String participantTyp);

    /**
     * This used for testing the {@link casp.web.backend.presentation.layer.rest.CalendarRestController#setMemberEntriesAndEventsStatus(UUID, EntityStatus)}
     *
     * @param baseEvent instance of type {@link BaseEvent}
     * @return a set of {@link BaseParticipant}s
     */
    Set<BaseParticipant> findAllByBaseEvent(BaseEvent baseEvent);

    Set<BaseParticipant> findAllByBaseEventInAndEntityStatusNotLike(Set<BaseEvent> baseEventSet, EntityStatus entityStatus);

    Set<BaseParticipant> findAllByBaseEventInAndEntityStatusNotLikeAndParticipantType(Set<BaseEvent> baseEventSet,
                                                                                      EntityStatus entityStatus,
                                                                                      String participantType);

    Set<BaseParticipant> findAllByEntityStatusAndParticipantType(EntityStatus entityStatus, String participantType);
}
