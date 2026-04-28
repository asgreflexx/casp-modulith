package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.CalendarValidation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class CalendarEntryDto implements Comparable<CalendarEntryDto>, CalendarValidation {
    private UUID id;
    @Deprecated(forRemoval = true, since = "2026-04-23")
    private LocalDateTime entryFrom;
    @Deprecated(forRemoval = true, since = "2026-04-23")
    private LocalDateTime entryTo;
    // TODO should be not null
    private OffsetDateTime entryFromODT;
    // TODO should be not null
    private OffsetDateTime entryToODT;
    private UUID baseEventId;
    private BaseEventType eventType;
    private String name;

    CalendarEntryDto(CalendarEntry calendarEntry, BaseEvent<?> baseEvent) {
        this.id = calendarEntry.getId();
        this.entryFrom = calendarEntry.getEntryFrom();
        this.entryTo = calendarEntry.getEntryTo();
        this.entryFromODT = calendarEntry.getEntryFromODT();
        this.entryToODT = calendarEntry.getEntryToODT();
        this.baseEventId = baseEvent.getId();
        this.eventType = baseEvent.getEventType();
        this.name = baseEvent.getName();
    }

    @Override
    public int compareTo(CalendarEntryDto calendarEntryDto) {
        return entryFromODT.compareTo(calendarEntryDto.entryFromODT) + entryToODT.compareTo(calendarEntryDto.entryToODT);
    }
}
