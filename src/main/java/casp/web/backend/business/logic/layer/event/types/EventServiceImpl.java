package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.event.types.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class EventServiceImpl extends BaseEventServiceImpl<Event, EventParticipant> implements EventService {

    @Autowired
    EventServiceImpl(final CalendarService calendarService,
                     final EventParticipantService participantService,
                     final BaseEventRepository eventRepository) {
        super(calendarService, participantService, eventRepository, Event.EVENT_TYPE);
    }
}
