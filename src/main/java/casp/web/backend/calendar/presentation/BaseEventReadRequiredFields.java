package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.data.CalendarEntry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public interface BaseEventReadRequiredFields {
    @NotNull
    LocalDateTime getMinTime();

    void setMinTime(@NotNull LocalDateTime minTime);

    @NotNull
    LocalDateTime getMaxTime();

    void setMaxTime(@NotNull LocalDateTime maxTime);

    @Valid
    @NotEmpty
    List<CalendarEntry> getCalendarEntries();

    void setCalendarEntries(@Valid @NotEmpty List<CalendarEntry> calendarEntries);
}
