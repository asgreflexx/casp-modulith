package casp.web.backend.calendar.data;

import java.time.OffsetDateTime;

@CalendarFromToConstraint
public interface CalendarValidation {
    OffsetDateTime getEntryFromODT();

    OffsetDateTime getEntryToODT();
}
