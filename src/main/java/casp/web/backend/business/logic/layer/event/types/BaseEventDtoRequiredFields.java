package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import jakarta.validation.Valid;

import java.util.UUID;

public interface BaseEventDtoRequiredFields extends BaseEventRequiredFields {
    UUID getNewMemberId();

    void setNewMemberId(UUID newMemberId);

    @Valid
    CalendarEntry getNewCalendarEntry();

    void setNewCalendarEntry(@Valid CalendarEntry newCalendarEntry);
}
