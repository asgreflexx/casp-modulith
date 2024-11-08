package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.BaseEventObserver;
import casp.web.backend.business.logic.layer.event.types.CalendarEntryDto;
import casp.web.backend.common.enums.BaseEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("calendar")
class CalendarRestController {
    private final BaseEventObserver baseEventObserver;

    @Autowired
    CalendarRestController(BaseEventObserver baseEventObserver) {
        this.baseEventObserver = baseEventObserver;
    }

    @GetMapping
    ResponseEntity<List<CalendarEntryDto>> getCalendarEntries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                                              @RequestParam(required = false) Set<BaseEventType> eventTypes) {
        var eventTypeSet = Optional.ofNullable(eventTypes).orElseGet(Collections::emptySet);
        var atStartOfDay = from.atStartOfDay();
        var atEndOfDay = to.atTime(LocalTime.MAX);
        var calendarEntries = baseEventObserver
                .getCalendarEntriesBetweenFromAndTo(atStartOfDay, atEndOfDay, eventTypeSet)
                .sorted()
                .toList();
        return ResponseEntity.ok(calendarEntries);
    }
}
