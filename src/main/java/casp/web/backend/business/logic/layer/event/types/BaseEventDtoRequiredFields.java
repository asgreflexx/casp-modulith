package casp.web.backend.business.logic.layer.event.types;

import jakarta.validation.Valid;

import java.util.UUID;

public interface BaseEventDtoRequiredFields {
    UUID getNewMemberId();

    void setNewMemberId(UUID newMemberId);

    @Valid
    NewCalendarEntryDto getNewCalendarEntry();

    void setNewCalendarEntry(@Valid NewCalendarEntryDto newCalendarEntry);
}
