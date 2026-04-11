package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.CalendarValidation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(of = "id")
public class CalendarEntryDto implements Comparable<CalendarEntryDto>, CalendarValidation {
    @Getter
    @Setter
    private UUID id;
    private LocalDateTime entryFrom;
    private LocalDateTime entryTo;
    @Getter
    @Setter
    private UUID baseEventId;
    @Getter
    @Setter
    private BaseEventType eventType;
    @Setter
    @Getter
    private String name;

    public CalendarEntryDto() {
    }

    public CalendarEntryDto(CalendarEntry calendarEntry, BaseEvent<?> baseEvent) {
        this.id = calendarEntry.getId();
        this.entryFrom = calendarEntry.getEntryFrom();
        this.entryTo = calendarEntry.getEntryTo();
        this.baseEventId = baseEvent.getId();
        this.eventType = baseEvent.getEventType();
        this.name = baseEvent.getName();
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
    public int compareTo(CalendarEntryDto calendarEntryDto) {
        return entryFrom.compareTo(calendarEntryDto.entryFrom) + entryTo.compareTo(calendarEntryDto.entryTo);
    }
}
