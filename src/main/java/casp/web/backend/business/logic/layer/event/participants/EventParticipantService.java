package casp.web.backend.business.logic.layer.event.participants;


import casp.web.backend.data.access.layer.documents.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Event;

import java.util.Set;
import java.util.UUID;

public interface EventParticipantService extends BaseParticipantService<EventParticipant, Event> {
    Set<ParticipantMember> getActiveEventParticipantsIfMembersAreActive(UUID baseEventId);
}
