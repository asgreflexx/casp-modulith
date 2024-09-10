package casp.web.backend.business.logic.layer.events.types;


import casp.web.backend.data.access.layer.documents.event.participant.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.presentation.layer.dtos.events.EventDto;

public interface EventService extends BaseEventService<Event, EventParticipant, EventDto> {

}
