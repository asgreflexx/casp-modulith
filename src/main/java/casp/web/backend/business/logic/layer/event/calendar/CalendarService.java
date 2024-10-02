package casp.web.backend.business.logic.layer.event.calendar;

import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CalendarService {

    /**
     * Get an active calendar entry by id or throws an exception if not found.
     *
     * @param id the id of the calendar entry
     * @return the calendar entry, or throws an exception if the calendar entry is not found
     */
    Calendar getCalendarEntryById(UUID id);

    List<Calendar> getCalendarEntriesByPeriodAndEventTypes(LocalDate calendarEntriesFrom, LocalDate calendarEntriesTo, @NotNull Set<String> eventTypes);

    /**
     * Delete all calendar entries for a given base event, creates new calendar entries and sets BaseEvent#minLocalDateTime and BaseEvent#maxLocalDateTime.
     *
     * @param baseEvent       the base event for which the calendar entries should be replaced
     * @param calendarEntries the new calendar entries
     */
    void replaceCalendarEntries(BaseEvent baseEvent, List<Calendar> calendarEntries);

    List<Calendar> getCalendarEntriesByBaseEvent(BaseEvent baseEvent);

    Calendar saveCalendarEntry(Calendar calendarEntry);

    void deleteCalendarEntryById(UUID id);

    /**
     * Set all calendar entries status with the given base event id to delete.
     * This is used when a base event is deleted.
     *
     * @param baseEventId the id of the base event
     */
    void deleteCalendarEntriesByBaseEventId(UUID baseEventId);

    /**
     * Set all calendar entries status with the given base event id to inactive.
     * This is used when a base event is deactivated.
     *
     * @param baseEventId the id of the base event
     */
    void deactivateCalendarEntriesByBaseEventId(UUID baseEventId);

    /**
     * Set all calendar entries status with the given base event id to active.
     * This is used when a base event is activated.
     *
     * @param baseEventId the id of the base event
     */
    void activateCalendarEntriesByBaseEventId(UUID baseEventId);

}
