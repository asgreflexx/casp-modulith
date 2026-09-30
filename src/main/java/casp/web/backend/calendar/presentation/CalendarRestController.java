package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.CalendarEntryDto;
import casp.web.backend.calendar.CalendarService;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("calendar")
class CalendarRestController {
    private final ZoneId zoneId;
    private final CalendarService calendarService;

    @GetMapping
    ResponseEntity<List<CalendarEntryDto>> getCalendarEntries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                                              @RequestParam(required = false) UUID memberId) {
        var atStartOfDay = from.atStartOfDay().atZone(zoneId).toOffsetDateTime();
        var atEndOfDay = to.atTime(LocalTime.MAX).atZone(zoneId).toOffsetDateTime();
        var calendarEntries = calendarService.findCalendarEntriesByFromAndToAndMemberId(atStartOfDay, atEndOfDay, memberId);
        return ResponseEntity.ok(calendarEntries);
    }
}
