package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.CalendarValidation;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class CalendarEntryDto implements Comparable<CalendarEntryDto>, CalendarValidation {
    private UUID id;
    @NotNull
    private OffsetDateTime entryFromODT;
    @NotNull
    private OffsetDateTime entryToODT;
    private UUID baseEventId;
    private BaseEventType eventType;
    private String name;

    CalendarEntryDto(CalendarEntry calendarEntry, UUID baseEventId, BaseEventType eventType, String name) {
        this.id = calendarEntry.getId();
        this.entryFromODT = calendarEntry.getEntryFromODT();
        this.entryToODT = calendarEntry.getEntryToODT();
        this.baseEventId = baseEventId;
        this.eventType = eventType;
        this.name = name;
    }

    CalendarEntryDto(CalendarEntry calendarEntry, BaseEvent<?> baseEvent) {
        this(calendarEntry, baseEvent.getId(), baseEvent.getEventType(), baseEvent.getName());
    }

    @Override
    public int compareTo(CalendarEntryDto calendarEntryDto) {
        return entryFromODT.compareTo(calendarEntryDto.entryFromODT) + entryToODT.compareTo(calendarEntryDto.entryToODT);
    }
}
