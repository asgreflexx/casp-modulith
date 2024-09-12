package casp.web.backend.business.logic.layer.event.participants;


import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.types.Event;

import java.util.Set;
import java.util.UUID;

public interface EventParticipantService extends BaseParticipantService<EventParticipant, Event> {
    Set<ParticipantMember> getActiveEventParticipantsIfMembersAreActive(UUID baseEventId);
}
