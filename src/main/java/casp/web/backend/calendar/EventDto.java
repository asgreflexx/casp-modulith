package casp.web.backend.calendar;


import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.EventParticipant;

public class EventDto extends BaseEventDto<EventParticipant> {

    public EventDto() {
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
