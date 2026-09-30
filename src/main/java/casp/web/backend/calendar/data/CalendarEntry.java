package casp.web.backend.calendar.data;

import com.querydsl.core.annotations.QueryEmbeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@QueryEmbeddable
@Setter
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = {"entryFromODT", "entryToODT"})
@NoArgsConstructor
public class CalendarEntry implements Comparable<CalendarEntry>, CalendarValidation {
    private UUID id = UUID.randomUUID();
    @NotNull
    private OffsetDateTime entryFromODT;
    @NotNull
    private OffsetDateTime entryToODT;

    public CalendarEntry(OffsetDateTime entryFromODT, OffsetDateTime entryToODT) {
        this.entryFromODT = entryFromODT;
        this.entryToODT = entryToODT;
    }

    @Override
    public int compareTo(CalendarEntry calendar) {
        return entryFromODT.compareTo(calendar.entryFromODT) + entryToODT.compareTo(calendar.entryToODT);
    }
}
