package casp.web.backend.business.logic.layer.events.types;


import casp.web.backend.business.logic.layer.events.dtos.EventDto;
import casp.web.backend.data.access.layer.documents.event.participant.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Event;

public interface EventService extends BaseEventService<Event, EventParticipant, EventDto> {

}
