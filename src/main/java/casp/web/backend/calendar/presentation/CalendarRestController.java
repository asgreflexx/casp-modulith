package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.BaseEventObserver;
import casp.web.backend.calendar.CalendarEntryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


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
                                                              @RequestParam(required = false) UUID memberId) {
        var atStartOfDay = from.atStartOfDay();
        var atEndOfDay = to.atTime(LocalTime.MAX);
        var calendarEntries = baseEventObserver.getCalendarEntriesBetweenFromAndToOrMemberId(atStartOfDay, atEndOfDay, memberId);
        return ResponseEntity.ok(calendarEntries);
    }
}
