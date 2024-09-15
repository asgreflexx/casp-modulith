package casp.web.backend.business.logic.layer.event.calendar;

import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CalendarService {

    Calendar getCalendarEntryById(UUID id);

    List<Calendar> getCalendarEntriesByPeriodAndEventTypes(LocalDate calendarEntriesFrom, LocalDate calendarEntriesTo, @NotNull Set<String> eventTypes);

    void replaceCalendarEntries(BaseEvent baseEvent, List<Calendar> calendarEntries);

    List<Calendar> getCalendarEntriesByBaseEvent(BaseEvent baseEvent);

    Calendar saveCalendarEntry(Calendar calendarEntry);

    void deleteCalendarEntryById(UUID id);

    void deleteCalendarEntriesByBaseEventId(UUID baseEventId);

    void deactivateCalendarEntriesByBaseEventId(UUID baseEventId);

    void activateCalendarEntriesByBaseEventId(UUID baseEventId);

}
