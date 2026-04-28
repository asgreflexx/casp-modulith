package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarValidation;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
public class NewCalendarEntryDto implements CalendarValidation {
    @NotNull
    private OffsetDateTime entryFromODT;
    @NotNull
    private OffsetDateTime entryToODT;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewCalendarEntryDto that)) return false;
        return Objects.equals(entryFromODT, that.entryFromODT) && Objects.equals(entryToODT, that.entryToODT);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryFromODT, entryToODT);
    }
}
