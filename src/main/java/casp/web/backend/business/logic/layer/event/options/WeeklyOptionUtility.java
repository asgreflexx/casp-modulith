package casp.web.backend.business.logic.layer.event.options;

import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.WeeklyOption;
import casp.web.backend.data.access.layer.event.options.WeeklyRecurrenceOption;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class WeeklyOptionUtility {
    private WeeklyOptionUtility() {
    }

    static List<CalendarEntry> createCalendarEntries(final WeeklyRecurrenceOption option) {
        List<CalendarEntry> calendarList = new ArrayList<>();
        var occurrences = option.getOccurrences();
        var dayOfWeekSet = occurrences
                .stream()
                .map(WeeklyOption::getDayOfWeek)
                .collect(Collectors.toSet());

        var localDatePointer = option.getStartRecurrence();
        var endTime = occurrences.getLast().getEndTime();
        var end = LocalDateTime.of(option.getEndRecurrence(), endTime);

        do {
            if (dayOfWeekSet.contains(localDatePointer.getDayOfWeek())) {
                var skipList = false;
                for (var recurrence : occurrences) {
                    if (recurrence.getDayOfWeek() == localDatePointer.getDayOfWeek()) {
                        var eventFrom =
                                LocalDateTime.of(localDatePointer,
                                        recurrence.getStartTime());
                        var eventTo =
                                LocalDateTime.of(localDatePointer,
                                        recurrence.getEndTime());

                        calendarList.add(new CalendarEntry(eventFrom, eventTo));
                        skipList = true;
                    } else if (skipList) {
                        break;
                    }
                }
            }
            if (option.getRepeatEvery() > 1 && localDatePointer.getDayOfWeek() == DayOfWeek.SUNDAY) {
                localDatePointer = localDatePointer.plusWeeks(option.getRepeatEvery() - 1L);
            }
            localDatePointer = localDatePointer.plusDays(1);
        } while (!LocalDateTime.of(localDatePointer, endTime).isAfter(end));

        return calendarList;
    }
}
