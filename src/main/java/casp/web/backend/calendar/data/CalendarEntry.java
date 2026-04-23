package casp.web.backend.calendar.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class CalendarEntry implements Comparable<CalendarEntry>, CalendarValidation {
    @Id
    private UUID id = UUID.randomUUID();
    @Deprecated(forRemoval = true, since = "2026-04-23")
    private LocalDateTime entryFrom;
    @Deprecated(forRemoval = true, since = "2026-04-23")
    private LocalDateTime entryTo;
    // TODO should be not null
    private OffsetDateTime entryFromODT;
    // TODO should be not null
    private OffsetDateTime entryToODT;

    public CalendarEntry() {
    }

    @Deprecated(forRemoval = true, since = "2026-04-23")
    public CalendarEntry(LocalDateTime entryFrom, LocalDateTime entryTo) {
        this.entryFrom = entryFrom;
        this.entryTo = entryTo;
    }

    @Override
    public int compareTo(CalendarEntry calendar) {
        return entryFrom.compareTo(calendar.entryFrom) + entryTo.compareTo(calendar.entryTo);
    }
}
