package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.RecurrenceOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public interface BaseEventRequiredFields {
    @NotBlank
    String getName();

    void setName(@NotBlank String name);

    String getDescription();

    void setDescription(String description);

    String getLocation();

    void setLocation(String location);

    @Valid
    @NotNull
    MemberReference getMember();

    void setMember(@Valid @NotNull MemberReference member);

    @Valid
    RecurrenceOption getRecurrenceOption();

    void setRecurrenceOption(@Valid RecurrenceOption recurrenceOption);

    @NotNull
    BaseEventType getEventType();

    void setEventType(@NotNull BaseEventType eventType);

    @NotNull
    LocalDateTime getMinTime();

    void setMinTime(@NotNull LocalDateTime minTime);

    @NotNull
    LocalDateTime getMaxTime();

    void setMaxTime(@NotNull LocalDateTime maxTime);

    List<CalendarEntry> getCalendarEntries();

    void setCalendarEntries(List<CalendarEntry> calendarEntries);
}
