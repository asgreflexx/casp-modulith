package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.business.logic.layer.member.MemberService;
import casp.web.backend.data.access.layer.documents.event.TypesRegex;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.event.types.BaseEventDto;
import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import casp.web.backend.presentation.layer.dtos.event.types.EventDto;
import casp.web.backend.presentation.layer.dtos.event.types.ExamDto;
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

import static casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.CoTrainerMapper.CO_TRAINER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.EventParticipantMapper.EVENT_PARTICIPANT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.ExamParticipantMapper.EXAM_PARTICIPANT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.SpaceMapper.SPACE_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.CourseMapper.COURSE_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.EventMapper.EVENT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.ExamMapper.EXAM_MAPPER;
import static casp.web.backend.presentation.layer.dtos.member.MemberMapper.MEMBER_MAPPER;

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
    private final DogHasHandlerService dogHasHandlerService;

    @Autowired
    CalendarRestController(final CalendarService calendarService,
                           final CoTrainerService coTrainerService,
                           final EventParticipantService eventParticipantService,
                           final ExamParticipantService examParticipantService,
                           final SpaceService spaceService,
                           final MemberService memberService,
                           final DogHasHandlerService dogHasHandlerService) {
        this.calendarService = calendarService;
        this.coTrainerService = coTrainerService;
        this.eventParticipantService = eventParticipantService;
        this.examParticipantService = examParticipantService;
        this.spaceService = spaceService;
        this.memberService = memberService;
        this.dogHasHandlerService = dogHasHandlerService;
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
    <P> BaseEventDto<P> getCalendarEntry(final @PathVariable UUID id) {
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
        setSpaces(courseDto);
        setCoTrainers(courseDto);
        return courseDto;
    }

    private void setCoTrainers(final CourseDto courseDto) {
        var coTrainers = coTrainerService.getParticipantsByBaseEventId(courseDto.getId());
        var coTrainerDtoSet = CO_TRAINER_MAPPER.toDtoSet(coTrainers);
        coTrainerDtoSet.forEach(c -> {
            var member = memberService.getMemberById(c.getMemberOrHandlerId());
            c.setMember(MEMBER_MAPPER.toDto(member));
            c.setBaseEvent(null);
        });
        courseDto.setCoTrainers(coTrainerDtoSet);
    }

    private void setSpaces(final CourseDto courseDto) {
        var participants = spaceService.getParticipantsByBaseEventId(courseDto.getId());
        var spaceDtoSet = SPACE_MAPPER.toDtoSet(participants);
        spaceDtoSet.forEach(s -> {
            var dogHasHandler = dogHasHandlerService.getDogHasHandlerById(s.getMemberOrHandlerId());
            s.setDogHasHandler(DOG_HAS_HANDLER_MAPPER.toDto(dogHasHandler));
            s.setBaseEvent(null);
        });
        courseDto.setParticipants(spaceDtoSet);
    }

    private EventDto mapToEventDto(final BaseEvent baseEvent) {
        var eventDto = EVENT_MAPPER.toDto((Event) baseEvent);
        setEventParticipants(eventDto);
        return eventDto;
    }

    private void setEventParticipants(final EventDto eventDto) {
        var participants = eventParticipantService.getParticipantsByBaseEventId(eventDto.getId());
        var eventParticipantDtoSet = EVENT_PARTICIPANT_MAPPER.toDtoSet(participants);
        eventParticipantDtoSet.forEach(e -> {
            var member = memberService.getMemberById(e.getMemberOrHandlerId());
            e.setMember(MEMBER_MAPPER.toDto(member));
            e.setBaseEvent(null);
        });
        eventDto.setParticipants(eventParticipantDtoSet);
    }

    private ExamDto mapToExamDto(final BaseEvent baseEvent) {
        var examDto = EXAM_MAPPER.toDto((Exam) baseEvent);
        setExamParticipants(examDto);
        return examDto;
    }

    private void setExamParticipants(final ExamDto examDto) {
        var participants = examParticipantService.getParticipantsByBaseEventId(examDto.getId());
        var examParticipantDtoSet = EXAM_PARTICIPANT_MAPPER.toDtoSet(participants);
        examParticipantDtoSet.forEach(e -> {
            var dogHasHandler = dogHasHandlerService.getDogHasHandlerById(e.getMemberOrHandlerId());
            e.setDogHasHandler(DOG_HAS_HANDLER_MAPPER.toDto(dogHasHandler));
            e.setBaseEvent(null);
        });
        examDto.setParticipants(examParticipantDtoSet);
    }
}
