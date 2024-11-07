package casp.web.backend.presentation.layer.event;


import casp.web.backend.business.logic.layer.event.types.EventDtoRequiredFields;
import casp.web.backend.business.logic.layer.event.types.EventRequiredFields;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EventWrite extends BaseEventWrite implements EventRequiredFields, EventDtoRequiredFields {
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<UUID> newParticipants = new HashSet<>();
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<EventParticipant> participants = new HashSet<>();

    @Override
    public Set<UUID> getNewParticipants() {
        return newParticipants;
    }

    @Override
    public void setNewParticipants(Set<UUID> newParticipants) {
        this.newParticipants = newParticipants;
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
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
