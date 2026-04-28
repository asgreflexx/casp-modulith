package casp.web.backend.calendar.data;

import com.querydsl.core.annotations.QueryEmbeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@QueryEmbeddable
@Setter
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = {"entryFromODT", "entryToODT"})
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

    public CalendarEntry(OffsetDateTime entryFromODT, OffsetDateTime entryToODT) {
        this.entryFromODT = entryFromODT;
        this.entryToODT = entryToODT;
    }

    @Override
    public int compareTo(CalendarEntry calendar) {
        return entryFromODT.compareTo(calendar.entryFromODT) + entryToODT.compareTo(calendar.entryToODT);
    }
}
