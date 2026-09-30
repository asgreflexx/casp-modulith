package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.EventParticipant;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class EventRead extends BaseEventRead<EventParticipant> {

    EventRead() {
        super(BaseEventType.EVENT);
    }
}
