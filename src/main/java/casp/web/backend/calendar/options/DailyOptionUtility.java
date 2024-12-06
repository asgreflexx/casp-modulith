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
        var eventFrom = option.min();
        var end = option.max();
        do {
            var eventTo =
                    LocalDateTime.of(eventFrom.toLocalDate(), option.getEndTime());
            calendarList.add(new CalendarEntry(eventFrom, eventTo));
            eventFrom = eventFrom.plusDays(option.getRepeatEvery());
        } while (!eventFrom.isAfter(end));

        return calendarList;
    }
}
