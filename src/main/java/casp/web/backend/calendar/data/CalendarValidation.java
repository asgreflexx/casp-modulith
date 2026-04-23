package casp.web.backend.calendar.data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@CalendarFromToConstraint
public interface CalendarValidation {
    @Deprecated(forRemoval = true, since = "2026-04-23")
    LocalDateTime getEntryFrom();

    @Deprecated(forRemoval = true, since = "2026-04-23")
    void setEntryFrom(LocalDateTime entryFrom);

    @Deprecated(forRemoval = true, since = "2026-04-23")
    LocalDateTime getEntryTo();

    @Deprecated(forRemoval = true, since = "2026-04-23")
    void setEntryTo(LocalDateTime entryTo);

    // TODO should be not null
    OffsetDateTime getEntryFromODT();

    // TODO should be not null
    OffsetDateTime getEntryToODT();
}
