package casp.web.backend.business.logic.layer.event;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.Calendar;
import casp.web.backend.data.access.layer.documents.event.QCalendar;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.participant.QBaseParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import casp.web.backend.data.access.layer.repositories.CalendarRepository;
import casp.web.backend.presentation.layer.dtos.events.BaseEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

// https://javarevisited.blogspot.com/2017/04/difference-between-autowired-and-inject-annotation-in-spring-framework.html#:~:text=The%20%40Autowired%20annotation%20is%20used,auto-wiring%20in%20Spring%20framework.&text=The%20%40Inject%20annotation%20also%20serves,and%20%40Autowired%20is%20spring%20specific.
@Service
class CalendarServiceImpl implements CalendarService {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarServiceImpl.class);

    private final CalendarRepository calendarRepository;
    private final BaseEventRepository baseEventRepository;
    private final BaseParticipantRepository baseParticipantRepository;

    private final CourseService courseService;
    private final ExamService examService;
    private final EventService eventService;

    @Autowired
    CalendarServiceImpl(CalendarRepository calendarRepository,
                        BaseEventRepository baseEventRepository,
                        BaseParticipantRepository baseParticipantRepository,
                        CourseService courseService,
                        ExamService examService,
                        EventService eventService) {
        this.calendarRepository = calendarRepository;
        this.baseEventRepository = baseEventRepository;
        this.baseParticipantRepository = baseParticipantRepository;
        this.courseService = courseService;
        this.examService = examService;
        this.eventService = eventService;
    }


    @Transactional(readOnly = true)
    @Override
    public <P extends BaseParticipant> BaseEventDto<P> getBaseEventEntryById(final UUID calendarId) {
        final var calendar = calendarRepository.findCalendarByEntityStatusAndId(EntityStatus.ACTIVE, calendarId).orElseThrow(() -> {
            var msg = "This calendar entry[%s] doesn't exist or it isn't active".formatted(calendarId);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
        final var baseEvent = calendar.getBaseEvent();
        final var baseEventId = baseEvent.getId();
        var baseEventDto = switch (baseEvent.getEventType()) {
            case Exam.EVENT_TYPE -> examService.getActiveEventDtoById(baseEventId);
            case Course.EVENT_TYPE -> courseService.getActiveEventDtoById(baseEventId);
            default -> eventService.getActiveEventDtoById(baseEventId);
        };
        baseEventDto.getEntries().add(calendar);
        return (BaseEventDto<P>) baseEventDto;
    }

    @Override
    public List<Calendar> getCalendarEntriesByPeriodAndEventTypes(final LocalDate eventFrom, final LocalDate eventTo, final Set<String> eventTypes) {
        var from = LocalDateTime.of(eventFrom, LocalTime.MIN);
        var to = LocalDateTime.of(eventTo, LocalTime.MAX);
        var qCalendar = QCalendar.calendar;
        var expression = qCalendar.entityStatus.eq(EntityStatus.ACTIVE)
                .and(qCalendar.eventFrom.goe(from))
                .and(qCalendar.eventTo.loe(to));

        if (!CollectionUtils.isEmpty(eventTypes)) {
            expression = expression.and(qCalendar.baseEvent.eventType.in(eventTypes));
        }
        return StreamSupport.stream(calendarRepository.findAll(expression).spliterator(), false)
                .toList();
    }

    @Transactional
    @Override
    public void setMemberEntriesAndEventsStatus(final UUID memberId, final EntityStatus entityStatus) {
        var events = baseEventRepository.findAllByMemberIdAndEntityStatusNotLike(memberId, EntityStatus.DELETED);
        setEventsEntityStatus(events, entityStatus);
        setParticipantsEntityStatus(memberId, events, entityStatus);
    }

    private void setEventsEntityStatus(final Set<BaseEvent> events, final EntityStatus entityStatus) {
        if (!CollectionUtils.isEmpty(events)) {
            calendarRepository.findAllByBaseEventInAndEntityStatusNotLike(events, EntityStatus.DELETED).forEach(c -> c.setEntityStatus(entityStatus));
            events.forEach(e -> e.setEntityStatus(entityStatus));
        }
    }

    private void setParticipantsEntityStatus(final UUID memberId, final Set<BaseEvent> events, final EntityStatus entityStatus) {
        final var qBaseParticipant = QBaseParticipant.baseParticipant;
        final var expression = qBaseParticipant.entityStatus.ne(EntityStatus.DELETED)
                .and(qBaseParticipant.memberOrHandlerId.eq(memberId).or(qBaseParticipant.baseEvent.in(events)));
        baseParticipantRepository.findAll(expression).forEach(p -> p.setEntityStatus(entityStatus));
    }
}
