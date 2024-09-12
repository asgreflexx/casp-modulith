package casp.web.backend.business.logic.layer.event.participants;


import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.types.BaseEvent;

import java.util.Set;
import java.util.UUID;

public interface BaseParticipantService<P extends BaseParticipant, E extends BaseEvent> {

    Set<P> saveParticipants(Set<P> participants, E baseEvent);

    Set<P> getParticipantsByBaseEventId(final UUID baseEventId);

    void deleteParticipantsByBaseEventId(UUID baseEventId);

    void deactivateParticipantsByBaseEventId(UUID baseEventId);

    void activateParticipantsByBaseEventId(UUID baseEventId);

    void deleteParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);

    void deactivateParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);

    void activateParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);
}
