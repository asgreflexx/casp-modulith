package casp.web.backend.calendar.options;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.WeeklyOption;
import casp.web.backend.calendar.data.options.WeeklyRecurrenceOption;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

enum WeeklyOptionUtility {
    ;

    static List<CalendarEntry> createCalendarEntries(WeeklyRecurrenceOption option) {
        List<CalendarEntry> calendarEntries = new ArrayList<>();
        var current = option.min();
        var end = option.max();
        while (!current.isAfter(end)) {
            calendarEntries.addAll(createCalendarEntries(current, option.getOccurrences(), end));
            current = current.plusWeeks(option.getRepeatEvery());
        }
        return calendarEntries;
    }

    private static List<CalendarEntry> createCalendarEntries(final LocalDateTime current, final List<WeeklyOption> occurrences, final LocalDateTime end) {
        return occurrences
                .stream()
                .map(weeklyOption -> createCalendarEntry(weeklyOption, current.toLocalDate()))
                .sorted()
                .filter(ce -> !ce.getEntryTo().isAfter(end))
                .toList();
    }

    private static CalendarEntry createCalendarEntry(final WeeklyOption weeklyOption, final LocalDate current) {
        if (current.getDayOfWeek() == weeklyOption.getDayOfWeek()) {
            var entryFrom = LocalDateTime.of(current, weeklyOption.getStartTime());
            var entryTo = LocalDateTime.of(current, weeklyOption.getEndTime());
            return new CalendarEntry(entryFrom, entryTo);
        } else {
            return createCalendarEntry(weeklyOption, current.plusDays(1));
        }
    }
}
