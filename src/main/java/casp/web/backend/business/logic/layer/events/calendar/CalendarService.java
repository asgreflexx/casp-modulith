package casp.web.backend.business.logic.layer.events.calendar;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CalendarService {

    Calendar getCalendarEntryById(UUID id);

    List<Calendar> getCalendarEntriesByPeriodAndEventTypes(LocalDate calendarEntriesFrom, LocalDate calendarEntriesTo, @NotNull Set<String> eventTypes);

    List<Calendar> replaceCalendarEntriesFromEvent(BaseEvent baseEvent, Calendar calendarEntry);

    List<Calendar> getCalendarEntriesByBaseEvent(BaseEvent baseEvent);

    Calendar saveCalendarEntry(Calendar calendarEntry);

    void deleteCalendarEntryById(UUID id);

    void deleteCalendarEntriesByBaseEventId(UUID baseEventId);

    void deactivateCalendarEntriesByBaseEventId(UUID baseEventId);

    void activateCalendarEntriesByBaseEventId(UUID baseEventId);

}
