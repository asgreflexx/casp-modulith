package casp.web.backend.business.logic.layer.events.options;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.options.DailyEventOption;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

class DailyEventOptionServiceImpl {
    private static final Logger LOG = LoggerFactory.getLogger(DailyEventOptionServiceImpl.class);

    List<Calendar> createCalendarEntries(String location, BaseEvent baseEvent) {
        LOG.debug("Parse the daily event options {}", baseEvent.getDailyOption());

        List<Calendar> calendarList = new ArrayList<>();
        DailyEventOption dailyEventOption = baseEvent.getDailyOption();
        LocalDateTime eventFrom =
                LocalDateTime.of(dailyEventOption.getStartRecurrence(), dailyEventOption.getStartTime());
        LocalDateTime eventTo =
                LocalDateTime.of(eventFrom.toLocalDate(), dailyEventOption.getEndTime());
        LocalDateTime end =
                LocalDateTime.of(dailyEventOption.getEndRecurrence(), dailyEventOption.getEndTime());

        do {

            calendarList.add(new Calendar(eventFrom, eventTo, location, baseEvent));
            eventFrom = eventFrom.plusDays(dailyEventOption.getRepeatEvery());
            eventTo = eventTo.plusDays(dailyEventOption.getRepeatEvery());

        } while (end.toEpochSecond(ZoneOffset.UTC) >= eventTo.toEpochSecond(ZoneOffset.UTC));

        LOG.debug("{} calendar entries were created", calendarList.size());
        return calendarList;
    }
}
