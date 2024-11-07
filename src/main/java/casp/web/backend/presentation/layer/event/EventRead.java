package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.EventRequiredFields;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;

import java.util.Set;

public class EventRead extends BaseEventRead implements EventRequiredFields {
    private Set<EventParticipant> participants;

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
