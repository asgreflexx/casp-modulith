package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Event;
import casp.web.backend.calendar.data.EventRepository;
import casp.web.backend.calendar.data.participants.EventParticipant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

import static casp.web.backend.calendar.EventMapper.EVENT_MAPPER;

@Service
class EventServiceImpl extends BaseEventServiceImpl<Event, EventDto, EventParticipant> implements EventService {

    @Autowired
    EventServiceImpl(EventRepository eventRepository) {
        super(eventRepository);
    }

    @Override
    public void save(EventDto dto) {
        var event = EVENT_MAPPER.toSource(dto);
        setCalendarEntriesAndMember(dto, event);
        setParticipants(dto, event);

        baseRepository.save(event);
    }

    @Override
    public EventDto getOneById(UUID id) {
        return EVENT_MAPPER.toTarget(getOneByIdOrThrowException(id));
    }

    @Override
    Stream<EventParticipant> mapToParticipant(UUID id) {
        return findMemberReferenceById(id).map(EventParticipant::new).stream();
    }
}
