package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.EventRequiredFields;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.EventParticipant;

import java.util.Set;

public class EventRead extends BaseEventRead implements EventRequiredFields {
    private Set<EventParticipant> participants;

    EventRead() {
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
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
