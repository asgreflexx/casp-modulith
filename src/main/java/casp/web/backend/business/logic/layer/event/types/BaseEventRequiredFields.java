package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public interface BaseEventRequiredFields {
    @NotNull
    BaseEventType getEventType();

    void setEventType(@NotNull BaseEventType eventType);

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
