package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.BaseParticipantService;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

abstract class BaseEventServiceImpl<E extends BaseEvent, P extends BaseParticipant> implements BaseEventService<E> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEventServiceImpl.class);
    protected final CalendarService calendarService;
    protected final BaseParticipantService<P, E> participantService;
    protected final BaseEventRepository eventRepository;
    protected final MemberRepository memberRepository;
    protected final String eventType;

    protected BaseEventServiceImpl(final CalendarService calendarService,
                                   final BaseParticipantService<P, E> participantService,
                                   final BaseEventRepository eventRepository,
                                   final MemberRepository memberRepository,
                                   final String eventType) {
        this.calendarService = calendarService;
        this.participantService = participantService;
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
        this.eventType = eventType;
    }

    @Override
    public void deleteById(final UUID id) {
        deleteBaseEvent(getOneByIdOrThrowException(id));
    }

    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndNotDeleted(memberId).forEach(this::deleteBaseEvent);
    }

    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsActive(memberId).forEach(this::deactivateBaseEvent);
    }

    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsInactive(memberId).forEach(this::activateBaseEvent);

    }

    @Override
    public E getOneById(final UUID id) {
        var baseEvent = getOneByIdOrThrowException(id);
        if (baseEvent.getMember() == null) {
            setMemberIfNull(baseEvent);
        }
        return baseEvent;
    }

    // It cast to the correct type
    @SuppressWarnings("unchecked")
    @Override
    public Page<E> getAllByYear(final int year, final Pageable pageable) {
        return (Page<E>) eventRepository.findAllByYearAndEventType(year, eventType, pageable);
    }

    @Override
    public E save(final E actualBaseEvent) {
        setMemberIfNull(actualBaseEvent);
        return eventRepository.save(actualBaseEvent);
    }

    protected Set<BaseEvent> findAllByMemberIdAndNotDeleted(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, eventType);
    }

    protected Set<BaseEvent> findAllByMemberIdAndIsActive(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, eventType);
    }

    protected Set<BaseEvent> findAllByMemberIdAndIsInactive(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, eventType);
    }

    protected void deleteBaseEvent(final BaseEvent baseEvent) {
        participantService.deleteParticipantsByBaseEventId(baseEvent.getId());
        calendarService.deleteCalendarEntriesByBaseEventId(baseEvent.getId());
        baseEvent.setEntityStatus(EntityStatus.DELETED);
        eventRepository.save(baseEvent);
    }

    protected void deactivateBaseEvent(final BaseEvent baseEvent) {
        participantService.deactivateParticipantsByBaseEventId(baseEvent.getId());
        calendarService.deactivateCalendarEntriesByBaseEventId(baseEvent.getId());
        baseEvent.setEntityStatus(EntityStatus.INACTIVE);
        eventRepository.save(baseEvent);
    }

    protected void activateBaseEvent(final BaseEvent baseEvent) {
        participantService.activateParticipantsByBaseEventId(baseEvent.getId());
        calendarService.activateCalendarEntriesByBaseEventId(baseEvent.getId());
        baseEvent.setEntityStatus(EntityStatus.ACTIVE);
        eventRepository.save(baseEvent);
    }

    // It cast to the correct type
    @SuppressWarnings("unchecked")
    protected E getOneByIdOrThrowException(final UUID id) {
        return (E) eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "This %s[%s] doesn't exist or it isn't active".formatted(eventType, id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    private void setMemberIfNull(final E baseEvent) {
        memberRepository.findByIdAndEntityStatus(baseEvent.getMemberId(), EntityStatus.ACTIVE)
                .ifPresent(baseEvent::setMember);
    }
}
