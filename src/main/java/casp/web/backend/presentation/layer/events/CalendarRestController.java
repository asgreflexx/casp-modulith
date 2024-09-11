package casp.web.backend.presentation.layer.events;

import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.events.participants.EventParticipantService;
import casp.web.backend.business.logic.layer.events.participants.ExamParticipantService;
import casp.web.backend.business.logic.layer.events.participants.SpaceService;
import casp.web.backend.business.logic.layer.member.MemberService;
import casp.web.backend.data.access.layer.documents.event.TypesRegex;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.events.types.BaseEventDto;
import casp.web.backend.presentation.layer.dtos.events.types.CourseDto;
import casp.web.backend.presentation.layer.dtos.events.types.EventDto;
import casp.web.backend.presentation.layer.dtos.events.types.ExamDto;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.events.types.CourseMapper.COURSE_MAPPER;
import static casp.web.backend.presentation.layer.dtos.events.types.EventMapper.EVENT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.events.types.ExamMapper.EXAM_MAPPER;

@RestController
@RequestMapping("/calendar")
@Validated
class CalendarRestController {

    private final CalendarService calendarService;
    private final CoTrainerService coTrainerService;
    private final EventParticipantService eventParticipantService;
    private final ExamParticipantService examParticipantService;
    private final SpaceService spaceService;
    private final MemberService memberService;

    @Autowired
    CalendarRestController(final CalendarService calendarService,
                           final CoTrainerService coTrainerService,
                           final EventParticipantService eventParticipantService,
                           final ExamParticipantService examParticipantService,
                           final SpaceService spaceService,
                           final MemberService memberService) {
        this.calendarService = calendarService;
        this.coTrainerService = coTrainerService;
        this.eventParticipantService = eventParticipantService;
        this.examParticipantService = examParticipantService;
        this.spaceService = spaceService;
        this.memberService = memberService;
    }

    private static <P extends BaseParticipant> Set<P> removeBaseEventFromParticipants(final Set<P> participants) {
        participants.forEach(p -> p.setBaseEvent(null));
        return participants;
    }

    @GetMapping()
    List<Calendar> getEntriesByPeriodAndEventTypes(final @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                   LocalDate calendarEntriesFrom,
                                                   final @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                   LocalDate calendarEntriesTo,
                                                   final @RequestParam(required = false)
                                                   Set<@Pattern(regexp = TypesRegex.BASE_EVENT_TYPES_REGEX) String> eventTypes) {
        return calendarService.getCalendarEntriesByPeriodAndEventTypes(calendarEntriesFrom, calendarEntriesTo, eventTypes);
    }

    @GetMapping("/{id}")
    <P extends BaseParticipant> BaseEventDto<P> getCalendarEntry(final @PathVariable UUID id) {
        var calendarEntry = calendarService.getCalendarEntryById(id);
        var baseEvent = calendarEntry.getBaseEvent();
        setMemberIfMissing(baseEvent);
        var baseEventDto = switch (baseEvent.getEventType()) {
            case Course.EVENT_TYPE -> mapToCourseDto(baseEvent);
            case Event.EVENT_TYPE -> mapToEventDto(baseEvent);
            default -> mapToExamDto(baseEvent);
        };
        calendarEntry.setBaseEvent(null);
        baseEventDto.setCalendarEntries(List.of(calendarEntry));
        return (BaseEventDto<P>) baseEventDto;
    }

    private void setMemberIfMissing(final BaseEvent baseEvent) {
        if (null == baseEvent.getMember()) {
            baseEvent.setMember(memberService.getMemberById(baseEvent.getMemberId()));
        }
    }

    private CourseDto mapToCourseDto(final BaseEvent baseEvent) {
        var courseDto = COURSE_MAPPER.toDto((Course) baseEvent);
        var participants = spaceService.getParticipantsByBaseEventId(baseEvent.getId());
        courseDto.setParticipants(removeBaseEventFromParticipants(participants));
        var coTrainers = coTrainerService.getParticipantsByBaseEventId(baseEvent.getId());
        courseDto.setCoTrainers(removeBaseEventFromParticipants(coTrainers));
        return courseDto;
    }

    private EventDto mapToEventDto(final BaseEvent baseEvent) {
        var eventDto = EVENT_MAPPER.toDto((Event) baseEvent);
        var participants = eventParticipantService.getParticipantsByBaseEventId(baseEvent.getId());
        eventDto.setParticipants(removeBaseEventFromParticipants(participants));
        return eventDto;
    }

    private ExamDto mapToExamDto(final BaseEvent baseEvent) {
        var examDto = EXAM_MAPPER.toDto((Exam) baseEvent);
        var participants = examParticipantService.getParticipantsByBaseEventId(baseEvent.getId());
        examDto.setParticipants(removeBaseEventFromParticipants(participants));
        return examDto;
    }
}
