package casp.web.backend.calendar.data;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(of = "id")
public class CalendarEntry implements Comparable<CalendarEntry>, CalendarValidation {
    @Setter
    @Getter
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private LocalDateTime entryFrom;

    @NotNull
    private LocalDateTime entryTo;

    public CalendarEntry() {
    }

    public CalendarEntry(LocalDateTime entryFrom, LocalDateTime entryTo) {
        this.entryFrom = entryFrom;
        this.entryTo = entryTo;
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
}
