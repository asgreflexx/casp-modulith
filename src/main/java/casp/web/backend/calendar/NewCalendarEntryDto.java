package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarValidation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
public class NewCalendarEntryDto implements CalendarValidation {
    @Deprecated(forRemoval = true, since = "2026-04-23")
    private LocalDateTime entryFrom;
    @Deprecated(forRemoval = true, since = "2026-04-23")
    private LocalDateTime entryTo;
    // TODO should be not null
    private OffsetDateTime entryFromODT;
    // TODO should be not null
    private OffsetDateTime entryToODT;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewCalendarEntryDto that)) return false;
        return Objects.equals(entryFrom, that.entryFrom) && Objects.equals(entryTo, that.entryTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryFrom, entryTo);
    }
}
