package casp.web.backend.calendar;

import java.util.Set;
import java.util.UUID;

@EventParticipantsDtoConstraint
public interface EventDtoRequiredFields extends BaseEventDtoRequiredFields, EventRequiredFields {
    Set<UUID> getNewParticipants();

    void setNewParticipants(Set<UUID> newParticipants);
}
