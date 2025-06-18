package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Event;
import casp.web.backend.calendar.data.EventRepository;
import casp.web.backend.calendar.data.participants.EventParticipant;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
        // 1. Get existing active participants from the DB event that are also desired in the DTO's list
        var existingParticipants = getExistingParticipantsMatchingDtoParticipantIds(eventDto);

        // 2. Determine which participant IDs from the DTO are genuinely new (not already linked to the existing event)
        var newParticipants = getNewParticipants(eventDto, existingParticipants);

        // 3. Combine the existing participants (those already in the DB and still desired) with the new ones
        event.addParticipants(existingParticipants);
        event.addParticipants(newParticipants);
    }

    private Optional<EventParticipant> mapToParticipant(UUID memberId) {
        return findMemberReferenceById(memberId)
                .map(EventParticipant::new);
    }

    private Set<EventParticipant> getExistingParticipantsMatchingDtoParticipantIds(final EventDto eventDto) {
        return baseRepository.findOneByIdAndEntityStatus(eventDto.getId(), EntityStatus.ACTIVE)
                .stream()
                .flatMap(e -> e.getParticipants().stream())
                .filter(eventParticipant -> eventDto.getParticipantIds().contains(eventParticipant.getId()))
                .collect(Collectors.toSet());
    }

    private Set<EventParticipant> getNewParticipants(final EventDto eventDto, final Set<EventParticipant> existingParticipants) {
        var existingParticipantIds = existingParticipants.stream().map(EventParticipant::getId).collect(Collectors.toSet());
        return eventDto.getParticipantIds().stream()
                .filter(participantId -> !existingParticipantIds.contains(participantId))
                .flatMap(participantId -> mapToParticipant(participantId).stream())
                .collect(Collectors.toSet());
    }
}
