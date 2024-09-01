package casp.web.backend.business.logic.layer.events.types;

import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.dtos.EventDto;
import casp.web.backend.business.logic.layer.events.mappers.EventMapperImpl;
import casp.web.backend.business.logic.layer.events.participants.EventParticipantService;
import casp.web.backend.data.access.layer.documents.event.participant.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class EventServiceImpl extends BaseEventServiceImpl<Event, EventDto, EventParticipant> implements EventService {

    @Autowired
    EventServiceImpl(final CalendarService calendarService,
                     final EventParticipantService participantService,
                     final BaseEventRepository eventRepository) {
        super(calendarService, participantService, eventRepository, Event.EVENT_TYPE, new EventMapperImpl());
    }

    @Override
    public EventDto createNewBaseEventWithOneCalendarEntry() {
        return super.createNewEventWithOneCalendarEntry(new EventDto());
    }
}
