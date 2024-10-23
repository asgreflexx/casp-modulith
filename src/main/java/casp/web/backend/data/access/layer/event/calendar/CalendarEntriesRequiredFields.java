package casp.web.backend.data.access.layer.event.calendar;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface CalendarEntriesRequiredFields {
    @Valid
    @NotEmpty
    List<CalendarEntry> getCalendarEntries();

    void setCalendarEntries(@Valid @NotEmpty List<CalendarEntry> calendarEntries);
}
