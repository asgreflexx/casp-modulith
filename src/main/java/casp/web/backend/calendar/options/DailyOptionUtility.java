package casp.web.backend.calendar.options;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

enum DailyOptionUtility {
    ;

    static List<CalendarEntry> createCalendarEntries(DailyRecurrenceOption option) {
        List<CalendarEntry> calendarList = new ArrayList<>();
        var current = option.min();
        var end = option.max();
        while (!current.isAfter(end)) {
            var eventTo = LocalDateTime.of(current.toLocalDate(), option.getEndTime());
            calendarList.add(new CalendarEntry(current, eventTo));
            current = current.plusDays(option.getRepeatEvery());
        }
        return calendarList;
    }
}
