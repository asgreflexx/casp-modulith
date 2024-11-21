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
        var eventFrom =
                LocalDateTime.of(option.getStartRecurrence(), option.getStartTime());
        var eventTo =
                LocalDateTime.of(eventFrom.toLocalDate(), option.getEndTime());
        var end =
                LocalDateTime.of(option.getEndRecurrence(), option.getEndTime());

        do {
            calendarList.add(new CalendarEntry(eventFrom, eventTo));
            eventFrom = eventFrom.plusDays(option.getRepeatEvery());
            eventTo = eventTo.plusDays(option.getRepeatEvery());
        } while (!eventTo.isAfter(end));

        return calendarList;
    }
}
