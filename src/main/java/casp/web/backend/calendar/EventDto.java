package casp.web.backend.calendar;


import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.EventParticipant;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EventDto extends BaseEventDto implements EventDtoRequiredFields {
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
    public void setParticipants(Set<EventParticipant> participants) {
        this.participants = participants;
    }

    @Override
    public Set<UUID> getNewParticipants() {
        return newParticipants;
    }

    @Override
    public void setNewParticipants(Set<UUID> newParticipants) {
        this.newParticipants = newParticipants;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
