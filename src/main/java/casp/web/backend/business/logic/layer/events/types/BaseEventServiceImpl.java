package casp.web.backend.business.logic.layer.events.types;

import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.dtos.BaseEventDto;
import casp.web.backend.business.logic.layer.events.mappers.BaseEventMapper;
import casp.web.backend.business.logic.layer.events.participants.BaseParticipantService;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

abstract class BaseEventServiceImpl<E extends BaseEvent, D extends BaseEventDto<P>, P extends BaseParticipant> implements BaseEventService<E, P, D> {
    protected static final Logger LOG = LoggerFactory.getLogger(BaseEventServiceImpl.class);
    private static final int DEFAULT_EVENT_LENGTH = 1;
    private static final ZoneId ZONE_ID = TimeZone.getDefault().toZoneId();
    protected final CalendarService calendarService;
    protected final BaseParticipantService<P, E> participantService;
    protected final BaseEventRepository eventRepository;
    protected final String eventType;
    protected final BaseEventMapper<E, D> mapper;
    protected Set<P> participants;
    protected List<Calendar> calendarEntries;

    protected BaseEventServiceImpl(final CalendarService calendarService,
                                   final BaseParticipantService<P, E> participantService,
                                   final BaseEventRepository eventRepository,
                                   final String eventType,
                                   final BaseEventMapper<E, D> mapper) {
        this.calendarService = calendarService;
        this.participantService = participantService;
        this.eventRepository = eventRepository;
        this.eventType = eventType;
        this.mapper = mapper;
    }

    private static void deleteBaseEvent(final BaseEvent baseEvent) {
        // TODO delete  calendar entries (event);
        // TODO delete participants (event);
        baseEvent.setEntityStatus(EntityStatus.DELETED);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBaseEventById(final UUID id) {
        eventRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED).ifPresent(BaseEventServiceImpl::deleteBaseEvent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        eventRepository.findAllByMemberIdAndEntityStatusNot(memberId, EntityStatus.DELETED).forEach(BaseEventServiceImpl::deleteBaseEvent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        eventRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE).forEach(baseEvent -> {
            // TODO deactivate  calendar entries (event);
            // TODO deactivate participants (event);
            baseEvent.setEntityStatus(EntityStatus.INACTIVE);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        eventRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE).forEach(baseEvent -> {
            // TODO activate  calendar entries (event);
            // TODO activate participants (event);
            baseEvent.setEntityStatus(EntityStatus.ACTIVE);
        });

    }

    // It cast to the correct type
    @SuppressWarnings("unchecked")
    @Override
    public D getBaseEventDtoById(final UUID id) {
        var event = (E) eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> {
                    var msg = "This %s[%s] doesn't exist or it isn't active".formatted(eventType, id);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
        var dto = mapper.documentToDto(event);
        dto.setParticipants(participantService.getParticipantsByEvent(event));
        dto.setCalendarEntries(calendarService.getCalendarEntriesByEvent(event));
        return dto;
    }

    // It cast to the correct type
    @SuppressWarnings("unchecked")
    @Override
    public Page<E> getBaseEventsAsPage(final int year, final Pageable pageable) {
        return (Page<E>) eventRepository.findAllByYearAndEventType(year, eventType, pageable);
    }

    @Transactional
    @Override
    public D saveBaseEventDto(final D actualBaseEventDto) {
        E event = mapper.dtoToDocument(actualBaseEventDto);
        event = saveEvent(event, actualBaseEventDto.getParticipants(), actualBaseEventDto.getCalendarEntries());

        var newBaseEvent = mapper.documentToDto(event);
        newBaseEvent.setParticipants(participants);
        newBaseEvent.setCalendarEntries(calendarEntries);

        return newBaseEvent;
    }

    protected D createNewEventWithOneCalendarEntry(final D event) {
        var eventFrom = ZonedDateTime.now(ZONE_ID).toLocalDateTime().withNano(0);
        var calendar = new Calendar();
        calendar.setEventFrom(eventFrom);
        calendar.setEventTo(eventFrom.plusHours(DEFAULT_EVENT_LENGTH));
        event.getCalendarEntries().add(calendar);
        return event;
    }

    private E saveEvent(final E event, final Set<P> participants, final List<Calendar> calendarEntries) {
        this.calendarEntries = calendarService.replaceCalendarEntriesFromEvent(event, calendarEntries);
        this.participants = participantService.saveParticipants(participants);

        event.setMinLocalDateTime(calendarEntries.getFirst().getEventFrom());
        event.setMaxLocalDateTime(calendarEntries.getLast().getEventTo());
        event.setParticipantsSize(this.participants.size());
        return eventRepository.save(event);
    }
}
