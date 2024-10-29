package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.event.types.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.event.types.EventMapper.EVENT_MAPPER;

@Service
class EventServiceImpl extends BaseEventServiceImpl<Event, EventDto> implements EventService {

    @Autowired
    EventServiceImpl(final EventRepository eventRepository,
                     final MemberReferenceRepository memberReferenceRepository,
                     final BaseEventMigrationService migrationService) {
        super(memberReferenceRepository, eventRepository, null, migrationService);
    }

    @Override
    public void save(final EventDto dto) {
        var event = EVENT_MAPPER.toSource(dto);
        setCalendarEntriesAndMember(dto, event);
        setParticipants(dto, event);

        baseRepository.setMetadataAndSave(event);
    }

    private void setParticipants(final EventDto eventDto, final Event event) {
        var actualParticipants = event.getParticipants();
        var eventParticipantSet = eventDto.getNewParticipants()
                .stream()
                .flatMap(id -> findMemberReferenceById(id)
                        .map(EventParticipant::new)
                        .stream())
                .collect(Collectors.toSet());
        actualParticipants.addAll(eventParticipantSet);
        event.setParticipants(actualParticipants);
    }
}
