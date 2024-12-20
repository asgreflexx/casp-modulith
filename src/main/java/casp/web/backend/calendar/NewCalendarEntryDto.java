package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarValidation;

import java.time.LocalDateTime;
import java.util.Objects;

public class NewCalendarEntryDto implements CalendarValidation {
    private LocalDateTime entryFrom;
    private LocalDateTime entryTo;

    @Override
    public LocalDateTime getEntryFrom() {
        return entryFrom;
    }

    @Override
    public void setEntryFrom(LocalDateTime entryFrom) {
        this.entryFrom = entryFrom;
    }

    @Override
    public LocalDateTime getEntryTo() {
        return entryTo;
    }

    @Override
    public void setEntryTo(LocalDateTime entryTo) {
        this.entryTo = entryTo;
    }

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
