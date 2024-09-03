package casp.web.backend.business.logic.layer.events.options;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public final class EventOptionServiceUtility {
    private static final DailyEventOptionServiceImpl DAILY_EVENT_OPTION_SERVICE = new DailyEventOptionServiceImpl();
    private static final WeeklyEventOptionServiceImpl WEEKLY_EVENT_OPTION_SERVICE = new WeeklyEventOptionServiceImpl();

    private EventOptionServiceUtility() {
    }

    public static <E extends BaseEvent> List<Calendar> createCalendarEntries(E e, List<Calendar> entries) {
        List<Calendar> calendarEntries = new ArrayList<>();
        if (CollectionUtils.isEmpty(entries)) {
            throw new IllegalArgumentException("It needs at least 1 one calendar entry");
        }
        final Calendar firstEntry = entries.get(0);
        if (e.getDailyOption() != null || e.getWeeklyOption() != null) {
            if (e.getDailyOption() != null) {
                calendarEntries = DAILY_EVENT_OPTION_SERVICE.createCalendarEntries(firstEntry.getLocation(), e);
            } else {
                calendarEntries = WEEKLY_EVENT_OPTION_SERVICE.createCalendarEntries(firstEntry.getLocation(), e);
            }
        }
        if (CollectionUtils.isEmpty(calendarEntries)) {
            calendarEntries.add(new Calendar(firstEntry, e));
        }
        calendarEntries.sort(Calendar::compareTo);
        return calendarEntries;
    }
}
