package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.presentation.layer.dtos.event.participants.EventParticipantDto;

public class EventDto extends BaseEventDto<EventParticipantDto> {
    public EventDto() {
        super(Event.EVENT_TYPE);
    }
}
