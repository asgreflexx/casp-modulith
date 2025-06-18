package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.CalendarDtoConstraint;
import casp.web.backend.calendar.NewCalendarEntryDto;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@CalendarDtoConstraint
public interface BaseEventWriteRequiredFields {
    @NotBlank
    String getName();

    void setName(@NotBlank String name);

    String getDescription();

    void setDescription(String description);

    String getLocation();

    void setLocation(String location);

    @Valid
    NewCalendarEntryDto getNewCalendarEntry();

    void setNewCalendarEntry(@Valid NewCalendarEntryDto newCalendarEntry);

    @Valid
    RecurrenceOption getRecurrenceOption();

    void setRecurrenceOption(@Valid RecurrenceOption recurrenceOption);

    @NotNull
    UUID getMemberId();

    void setMemberId(@NotNull UUID memberId);
}
