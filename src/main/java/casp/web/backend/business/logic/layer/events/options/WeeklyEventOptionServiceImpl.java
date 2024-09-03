package casp.web.backend.business.logic.layer.events.options;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.options.WeeklyEventOptionRecurrence;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class WeeklyEventOptionServiceImpl {
    private static final Logger LOG = LoggerFactory.getLogger(WeeklyEventOptionServiceImpl.class);

    List<Calendar> createCalendarEntries(String location, BaseEvent baseEvent) {
        LOG.debug("Parse the weekly event options {}", baseEvent.getDailyOption());

        List<Calendar> calendarList = new ArrayList<>();
        var weeklyEventOption = baseEvent.getWeeklyOption();
        List<WeeklyEventOptionRecurrence> sortedList = new ArrayList<>(weeklyEventOption.getOccurrences());
        Collections.sort(sortedList);
        Set<DayOfWeek> dayOfWeekSet = sortedList
                .stream()
                .map(WeeklyEventOptionRecurrence::getDayOfWeek)
                .collect(Collectors.toSet());

        var localDatePointer = weeklyEventOption.getStartRecurrence();
        LocalTime endTime = sortedList.get(sortedList.size() - 1).getEndTime();
        var end = LocalDateTime.of(weeklyEventOption.getEndRecurrence(), endTime);

        do {
            if (dayOfWeekSet.contains(localDatePointer.getDayOfWeek())) {
                var skipList = false;

                for (WeeklyEventOptionRecurrence weeklyEventOptionRecurrence : sortedList) {

                    if (weeklyEventOptionRecurrence.getDayOfWeek() == localDatePointer.getDayOfWeek()) {
                        var eventFrom =
                                LocalDateTime.of(localDatePointer,
                                        weeklyEventOptionRecurrence.getStartTime());
                        var eventTo =
                                LocalDateTime.of(localDatePointer,
                                        weeklyEventOptionRecurrence.getEndTime());

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

        LOG.debug("{} calendar entries were created", calendarList.size());
        return calendarList;
    }
}
