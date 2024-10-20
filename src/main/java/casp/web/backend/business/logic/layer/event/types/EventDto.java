package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class EventDto extends BaseEventDto implements EventRequiredFields, EventDtoRequiredFields {
    private Set<EventParticipant> participants = new HashSet<>();
    private Set<UUID> newParticipants = new HashSet<>();

    public EventDto() {
        super(BaseEventType.EVENT);
    }

    @Override
    public Set<EventParticipant> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(final Set<EventParticipant> participants) {
        this.participants = participants;
    }

    @Override
    public Set<UUID> getNewParticipants() {
        return newParticipants;
    }

    @Override
    public void setNewParticipants(final Set<UUID> newParticipants) {
        this.newParticipants = newParticipants;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
