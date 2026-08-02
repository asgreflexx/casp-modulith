package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.calendar.data.options.WeeklyRecurrenceOption;

import java.time.ZoneId;
import java.util.List;

enum RecurrenceOptionUtility {
    ;

    static List<CalendarEntry> createCalendarEntries(RecurrenceOption option, ZoneId zoneId) {
        if (option instanceof DailyRecurrenceOption daily) {
            return DailyOptionUtility.createCalendarEntries(daily, zoneId);
        } else {
            return WeeklyOptionUtility.createCalendarEntries((WeeklyRecurrenceOption) option, zoneId);
        }
    }
}
