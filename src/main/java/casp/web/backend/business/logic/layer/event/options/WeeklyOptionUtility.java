package casp.web.backend.business.logic.layer.event.options;

import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOptionRecurrence;
import casp.web.backend.data.access.layer.event.types.BaseEvent;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class WeeklyOptionUtility {
    private WeeklyOptionUtility() {
    }

    static List<Calendar> createCalendarEntries(String location, BaseEvent baseEvent) {
        List<Calendar> calendarList = new ArrayList<>();
        var weeklyEventOption = baseEvent.getWeeklyOption();
        var occurrences = weeklyEventOption.getOccurrences();
        var dayOfWeekSet = occurrences
                .stream()
                .map(WeeklyEventOptionRecurrence::getDayOfWeek)
                .collect(Collectors.toSet());

        var localDatePointer = weeklyEventOption.getStartRecurrence();
        var endTime = occurrences.getLast().getEndTime();
        var end = LocalDateTime.of(weeklyEventOption.getEndRecurrence(), endTime);

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

                        calendarList.add(new Calendar(eventFrom, eventTo, location, baseEvent));
                        skipList = true;
                    } else if (skipList) {
                        break;
                    }
                }
            }
            if (weeklyEventOption.getRepeatEvery() > 1 && localDatePointer.getDayOfWeek() == DayOfWeek.SUNDAY) {
                localDatePointer = localDatePointer.plusWeeks(weeklyEventOption.getRepeatEvery() - 1L);
            }
            localDatePointer = localDatePointer.plusDays(1);
        } while (end.toEpochSecond(ZoneOffset.UTC) >=
                LocalDateTime.of(localDatePointer, endTime).toEpochSecond(ZoneOffset.UTC));

        return calendarList;
    }
}
