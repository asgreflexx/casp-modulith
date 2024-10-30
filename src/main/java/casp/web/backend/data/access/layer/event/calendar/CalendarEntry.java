package casp.web.backend.data.access.layer.event.calendar;

import casp.web.backend.common.validation.CalendarValidation;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


public class CalendarEntry implements Comparable<CalendarEntry>, CalendarValidation {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private LocalDateTime entryFrom;

    @NotNull
    private LocalDateTime entryTo;

    public CalendarEntry() {
    }

    public CalendarEntry(final LocalDateTime entryFrom, final LocalDateTime entryTo) {
        this.entryFrom = entryFrom;
        this.entryTo = entryTo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
    public int compareTo(CalendarEntry calendar) {
        return entryFrom.compareTo(calendar.entryFrom) + entryTo.compareTo(calendar.entryTo);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarEntry calendar)) return false;
        return Objects.equals(id, calendar.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
