package casp.web.backend.business.logic.layer.event.options;

import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.DailyRecurrenceOption;
import casp.web.backend.data.access.layer.event.options.RecurrenceOption;
import casp.web.backend.data.access.layer.event.options.WeeklyRecurrenceOption;

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
