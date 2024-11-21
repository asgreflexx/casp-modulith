package casp.web.backend.calendar.options;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.calendar.data.options.WeeklyRecurrenceOption;

import java.util.List;

public enum RecurrenceOptionUtility {
    ;

    public static List<CalendarEntry> createCalendarEntries(RecurrenceOption option) {
        if (option instanceof DailyRecurrenceOption daily) {
            return DailyOptionUtility.createCalendarEntries(daily);
        } else {
            return WeeklyOptionUtility.createCalendarEntries((WeeklyRecurrenceOption) option);
        }
    }
}
