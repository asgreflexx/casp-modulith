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
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("calendar")
class CalendarRestController {
    private final BaseEventObserver baseEventObserver;
    private final ZoneId zoneId;

    @Autowired
    CalendarRestController(BaseEventObserver baseEventObserver, ZoneId zoneId) {
        this.baseEventObserver = baseEventObserver;
        this.zoneId = zoneId;
    }

    @GetMapping
    ResponseEntity<List<CalendarEntryDto>> getCalendarEntries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                                              @RequestParam(required = false) UUID memberId) {
        var atStartOfDay = from.atStartOfDay().atZone(zoneId).toOffsetDateTime();
        var atEndOfDay = to.atTime(LocalTime.MAX).atZone(zoneId).toOffsetDateTime();
        var calendarEntries = baseEventObserver.getCalendarEntriesBetweenFromAndToOrMemberId(atStartOfDay, atEndOfDay, memberId);
        return ResponseEntity.ok(calendarEntries);
    }
}
