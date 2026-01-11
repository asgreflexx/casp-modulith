package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.CalendarValidation;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CalendarEntryDto implements Comparable<CalendarEntryDto>, CalendarValidation {
    private UUID id;
    private LocalDateTime entryFrom;
    private LocalDateTime entryTo;
    private UUID baseEventId;
    private BaseEventType eventType;
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

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getBaseEventId() {
        return baseEventId;
    }

    public void setBaseEventId(final UUID baseEventId) {
        this.baseEventId = baseEventId;
    }

    public BaseEventType getEventType() {
        return eventType;
    }

    public void setEventType(final BaseEventType eventType) {
        this.eventType = eventType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarEntryDto that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
