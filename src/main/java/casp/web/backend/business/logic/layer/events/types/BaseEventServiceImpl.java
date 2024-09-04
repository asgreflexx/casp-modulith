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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

abstract class BaseEventServiceImpl<E extends BaseEvent, D extends BaseEventDto<P>, P extends BaseParticipant> implements BaseEventService<E, P, D> {
    protected static final Logger LOG = LoggerFactory.getLogger(BaseEventServiceImpl.class);
    private static final int DEFAULT_EVENT_LENGTH = 1;
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

    protected static void deleteBaseEvent(final BaseEvent baseEvent) {
        // TODO delete  calendar entries (event);
        // TODO delete participants (event);
        baseEvent.setEntityStatus(EntityStatus.DELETED);
    }

    protected static void deactivateBaseEvent(final BaseEvent baseEvent) {
        // TODO deactivate  calendar entries (event);
        // TODO deactivate participants (event);
        baseEvent.setEntityStatus(EntityStatus.INACTIVE);
    }

    protected static void activateBaseEvent(final BaseEvent baseEvent) {
        // TODO activate  calendar entries (event);
        // TODO activate participants (event);
        baseEvent.setEntityStatus(EntityStatus.ACTIVE);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBaseEventById(final UUID id) {
        findBaseEventNotDeleted(id).ifPresent(BaseEventServiceImpl::deleteBaseEvent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndNotDeleted(memberId).forEach(BaseEventServiceImpl::deleteBaseEvent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsActive(memberId).forEach(BaseEventServiceImpl::deactivateBaseEvent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsInactive(memberId).forEach(BaseEventServiceImpl::activateBaseEvent);

    }

    // It cast to the correct type
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
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
        dto.setCalendarEntries(calendarService.getCalendarEntriesByBaseEvent(event));
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
        var eventFrom = LocalDateTime.now(ZoneId.systemDefault());
        var calendar = new Calendar();
        calendar.setEventFrom(eventFrom);
        calendar.setEventTo(eventFrom.plusHours(DEFAULT_EVENT_LENGTH));
        event.getCalendarEntries().add(calendar);
        return event;
    }

    protected Optional<BaseEvent> findBaseEventNotDeleted(final UUID id) {
        return eventRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    }

    protected Set<BaseEvent> findAllByMemberIdAndNotDeleted(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatusNot(memberId, EntityStatus.DELETED);
    }

    protected Set<BaseEvent> findAllByMemberIdAndIsActive(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE);
    }

    protected Set<BaseEvent> findAllByMemberIdAndIsInactive(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE);
    }

    private E saveEvent(final E event, final Set<P> participants, final List<Calendar> calendarEntries) {
        this.calendarEntries = calendarService.replaceCalendarEntriesFromEvent(event, calendarEntries.getFirst());
        this.participants = participantService.saveParticipants(participants);

        event.setMinLocalDateTime(this.calendarEntries.getFirst().getEventFrom());
        event.setMaxLocalDateTime(this.calendarEntries.getLast().getEventTo());
        event.setParticipantsSize(this.participants.size());
        return eventRepository.save(event);
    }
}
