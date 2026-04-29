package casp.web.backend.calendar.data;

import java.util.List;
import java.util.UUID;

record CalendarEntryProjection(UUID id,
                               List<CalendarEntry> calendarEntries,
                               BaseEventType eventType,
                               String name) {
}
