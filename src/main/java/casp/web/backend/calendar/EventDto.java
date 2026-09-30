package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.EventParticipant;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class EventDto extends BaseEventDto<EventParticipant> {

    public EventDto() {
        super(BaseEventType.EVENT);
    }
}
