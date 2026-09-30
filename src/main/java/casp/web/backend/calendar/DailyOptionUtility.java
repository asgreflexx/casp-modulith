package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

enum DailyOptionUtility {
    ;

    static List<CalendarEntry> createCalendarEntries(DailyRecurrenceOption option, ZoneId zoneId) {
        List<CalendarEntry> calendarList = new ArrayList<>();
        var current = LocalDateTime.of(option.getStartRecurrence(), option.getStartTime())
                .atZone(zoneId)
                .toOffsetDateTime();
        var end = LocalDateTime.of(option.getEndRecurrence(), option.getEndTime()).atZone(zoneId).toOffsetDateTime();
        while (!current.isAfter(end)) {
            var eventTo = LocalDateTime.of(current.toLocalDate(), option.getEndTime()).atZone(zoneId).toOffsetDateTime();
            calendarList.add(new CalendarEntry(current, eventTo));
            current = current.plusDays(option.getRepeatEvery());
        }
        return calendarList;
    }
}
