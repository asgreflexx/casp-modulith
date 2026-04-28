package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.WeeklyOption;
import casp.web.backend.calendar.data.options.WeeklyRecurrenceOption;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

enum WeeklyOptionUtility {
    ;

    static List<CalendarEntry> createCalendarEntries(WeeklyRecurrenceOption option, ZoneId zoneId) {
        List<CalendarEntry> calendarEntries = new ArrayList<>();
        var first = option.getOccurrences().getFirst();
        var current = LocalDateTime.of(option.getStartRecurrence(), first.getStartTime())
                .atZone(zoneId)
                .toOffsetDateTime();
        var last = option.getOccurrences().getLast();
        var end = LocalDateTime.of(option.getEndRecurrence(), last.getEndTime())
                .atZone(zoneId)
                .toOffsetDateTime();
        while (!current.isAfter(end)) {
            calendarEntries.addAll(createCalendarEntries(current, end, option.getOccurrences()));
            current = current.plusWeeks(option.getRepeatEvery());
        }
        return calendarEntries;
    }

    private static List<CalendarEntry> createCalendarEntries(OffsetDateTime current,
                                                             OffsetDateTime end,
                                                             List<WeeklyOption> occurrences) {
        return occurrences
                .stream()
                .map(weeklyOption -> createCalendarEntry(current, weeklyOption))
                .sorted()
                .filter(ce -> !ce.getEntryToODT().isAfter(end))
                .toList();
    }

    private static CalendarEntry createCalendarEntry(OffsetDateTime current, WeeklyOption weeklyOption) {
        if (current.getDayOfWeek() == weeklyOption.getDayOfWeek()) {
            var entryFrom = current.with(weeklyOption.getStartTime());
            var entryTo = current.with(weeklyOption.getEndTime());
            return new CalendarEntry(entryFrom, entryTo);
        } else {
            return createCalendarEntry(current.plusDays(1), weeklyOption);
        }
    }
}
