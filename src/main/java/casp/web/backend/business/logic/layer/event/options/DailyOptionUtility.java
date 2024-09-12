package casp.web.backend.business.logic.layer.event.options;

import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.types.BaseEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

final class DailyOptionUtility {

    private DailyOptionUtility() {
    }

    static List<Calendar> createCalendarEntries(String location, BaseEvent baseEvent) {
        List<Calendar> calendarList = new ArrayList<>();
        var dailyEventOption = baseEvent.getDailyOption();
        var eventFrom =
                LocalDateTime.of(dailyEventOption.getStartRecurrence(), dailyEventOption.getStartTime());
        var eventTo =
                LocalDateTime.of(eventFrom.toLocalDate(), dailyEventOption.getEndTime());
        var end =
                LocalDateTime.of(dailyEventOption.getEndRecurrence(), dailyEventOption.getEndTime());

        do {
            calendarList.add(new Calendar(eventFrom, eventTo, location, baseEvent));
            eventFrom = eventFrom.plusDays(dailyEventOption.getRepeatEvery());
            eventTo = eventTo.plusDays(dailyEventOption.getRepeatEvery());
        } while (end.toEpochSecond(ZoneOffset.UTC) >= eventTo.toEpochSecond(ZoneOffset.UTC));

        return calendarList;
    }
}
