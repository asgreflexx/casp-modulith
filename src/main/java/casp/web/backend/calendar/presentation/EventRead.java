package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.EventParticipant;

public class EventRead extends BaseEventRead<EventParticipant> {

    EventRead() {
        super(BaseEventType.EVENT);
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
