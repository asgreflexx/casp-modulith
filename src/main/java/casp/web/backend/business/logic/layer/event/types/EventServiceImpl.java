package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.options.RecurrenceOptionUtility;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.event.types.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.event.types.EventMapper.EVENT_MAPPER;

@Service
class EventServiceImpl implements EventService {
    private static final Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final BaseEventMigrationService migrationService;

    @Autowired
    EventServiceImpl(final EventRepository eventRepository,
                     final MemberReferenceRepository memberReferenceRepository,
                     final BaseEventMigrationService migrationService) {
        this.eventRepository = eventRepository;
        this.memberReferenceRepository = memberReferenceRepository;
        this.migrationService = migrationService;
    }

    private static void setDateEntries(final EventDto eventDto, final Event event) {
        if (null == eventDto.getRecurrenceOption()) {
            event.setCalendarEntries(new ArrayList<>(List.of(eventDto.getNewCalendarEntry())));
        } else {
            event.setCalendarEntries(RecurrenceOptionUtility.createCalendarEntries(eventDto.getRecurrenceOption()));
        }
    }

    @Override
    public void save(final EventDto eventDto) {
        var event = EVENT_MAPPER.toSource(eventDto);
        setDateEntries(eventDto, event);
        setMember(eventDto, event);
        setParticipants(eventDto, event);

        eventRepository.setMetadataAndSave(event);
    }

    @Override
    public void deleteById(final UUID id) {
        var event = eventRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> {
                    var msg = "Event with id %s does not exist or it is not active.".formatted(id);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });

        setNewEntityStatus(event, EntityStatus.DELETED);
    }

    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        eventRepository.findAllByMemberIdAndNotDeleted(memberId)
                .forEach(event -> setNewEntityStatus(event, EntityStatus.DELETED));

    }

    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        eventRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)
                .forEach(event -> setNewEntityStatus(event, EntityStatus.INACTIVE));
    }

    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        eventRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.INACTIVE)
                .forEach(event -> setNewEntityStatus(event, EntityStatus.ACTIVE));
    }

    @Override
    public void migrateDataToV2() {
        eventRepository.deleteAll();

        var eventSet = migrationService.mapToEventV2();

        eventRepository.saveAll(eventSet);
    }

    private void setParticipants(final EventDto eventDto, final Event event) {
        var actualParticipants = event.getParticipants();
        var eventParticipantSet = eventDto.getNewParticipants()
                .stream()
                .flatMap(id -> findMemberReferenceByMemberIdAndStatus(id)
                        .map(EventParticipant::new)
                        .stream())
                .collect(Collectors.toSet());
        actualParticipants.addAll(eventParticipantSet);
        event.setParticipants(actualParticipants);
    }

    private void setMember(final EventDto eventDto, final Event event) {
        if (eventDto.getNewMemberId() != null) {
            findMemberReferenceByMemberIdAndStatus(eventDto.getNewMemberId())
                    .ifPresent(event::setMember);
        }
    }

    private Optional<MemberReference> findMemberReferenceByMemberIdAndStatus(final UUID courseDto) {
        return memberReferenceRepository.findOneByIdAndEntityStatus(courseDto, EntityStatus.ACTIVE);
    }

    private void setNewEntityStatus(final Event event, final EntityStatus entityStatus) {
        event.setEntityStatus(entityStatus);
        eventRepository.save(event);
    }
}
