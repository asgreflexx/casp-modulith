package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.member.MemberService;
import casp.web.backend.data.access.layer.event.TypesRegex;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.presentation.layer.dtos.event.calendar.CalendarDto;
import casp.web.backend.presentation.layer.dtos.event.types.BaseEventDto;
import casp.web.backend.presentation.layer.event.facades.CourseFacade;
import casp.web.backend.presentation.layer.event.facades.EventFacade;
import casp.web.backend.presentation.layer.event.facades.ExamFacade;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;

@RestController
@RequestMapping("/calendar")
@Validated
class CalendarRestController {

    private final CalendarService calendarService;
    private final MemberService memberService;
    private final CourseFacade courseFacade;
    private final EventFacade eventFacade;
    private final ExamFacade examFacade;

    @Autowired
    CalendarRestController(final CalendarService calendarService,
                           final MemberService memberService,
                           final CourseFacade courseFacade,
                           final EventFacade eventFacade,
                           final ExamFacade examFacade) {
        this.calendarService = calendarService;
        this.memberService = memberService;
        this.courseFacade = courseFacade;
        this.eventFacade = eventFacade;
        this.examFacade = examFacade;
    }

    @GetMapping
    ResponseEntity<List<CalendarDto>> getEntriesByPeriodAndEventTypes(final @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                      LocalDate calendarEntriesFrom,
                                                                      final @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                      LocalDate calendarEntriesTo,
                                                                      final @RequestParam(required = false)
                                                                      Set<@Pattern(regexp = TypesRegex.BASE_EVENT_TYPES_REGEX) String> eventTypes) {
        var calendarEntryList = calendarService.getCalendarEntriesByPeriodAndEventTypes(calendarEntriesFrom, calendarEntriesTo, eventTypes)
                .stream()
                .map(ce -> {
                    memberService.setActiveMemberToBaseEvent(ce.getBaseEvent());
                    return CALENDAR_MAPPER.toDto(ce);
                })
                .toList();
        return ResponseEntity.ok(calendarEntryList);
    }

    @GetMapping("/{id}")
    <P> ResponseEntity<BaseEventDto<P>> getCalendarEntry(final @PathVariable UUID id) {
        var calendarEntry = calendarService.getCalendarEntryById(id);
        var baseEvent = calendarEntry.getBaseEvent();
        memberService.setActiveMemberToBaseEvent(baseEvent);
        var baseEventDto = switch (baseEvent.getEventType()) {
            case Course.EVENT_TYPE -> courseFacade.mapDocumentToDto(baseEvent);
            case Event.EVENT_TYPE -> eventFacade.mapDocumentToDto(baseEvent);
            default -> examFacade.mapDocumentToDto(baseEvent);
        };
        calendarEntry.setBaseEvent(null);
        baseEventDto.setCalendarEntries(CALENDAR_MAPPER.toDtoList(List.of(calendarEntry)));
        return ResponseEntity.ok((BaseEventDto<P>) baseEventDto);
    }
}
