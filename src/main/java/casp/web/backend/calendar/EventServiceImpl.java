package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Event;
import casp.web.backend.calendar.data.EventRepository;
import casp.web.backend.calendar.data.participants.EventParticipant;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.calendar.EventMapper.EVENT_MAPPER;

@Service
class EventServiceImpl extends BaseEventServiceImpl<Event, EventDto> implements EventService {

    @Autowired
    EventServiceImpl(EventRepository eventRepository,
                     MemberReferenceRepository memberReferenceRepository,
                     BaseEventMigrationService migrationService) {
        super(memberReferenceRepository, eventRepository, null, migrationService);
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

    private void setParticipants(EventDto eventDto, Event event) {
        var newParticipants = mapToParticipants(eventDto.getNewParticipants());
        event.addParticipants(newParticipants);
    }

    private Set<EventParticipant> mapToParticipants(final Set<UUID> memberIds) {
        return memberIds
                .stream()
                .flatMap(id -> findMemberReferenceById(id)
                        .map(EventParticipant::new)
                        .stream())
                .collect(Collectors.toSet());
    }
}
