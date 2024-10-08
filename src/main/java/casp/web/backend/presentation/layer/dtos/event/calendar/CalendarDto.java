package casp.web.backend.presentation.layer.dtos.event.calendar;


import casp.web.backend.data.access.layer.event.calendar.CalendarFromToConstraint;
import casp.web.backend.data.access.layer.event.calendar.CalendarValidation;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@CalendarFromToConstraint
public class CalendarDto implements Comparable<CalendarDto>, CalendarValidation {
    private UUID id = UUID.randomUUID();
    @NotNull
    private LocalDateTime eventFrom;
    @NotNull
    private LocalDateTime eventTo;
    private String location;
    private CalendarBaseEventDto baseEvent;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getEventFrom() {
        return eventFrom;
    }

    public void setEventFrom(final LocalDateTime eventFrom) {
        this.eventFrom = eventFrom;
    }

    @Override
    public LocalDateTime getEventTo() {
        return eventTo;
    }

    public void setEventTo(final LocalDateTime eventTo) {
        this.eventTo = eventTo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public CalendarBaseEventDto getBaseEvent() {
        return baseEvent;
    }

    public void setBaseEvent(final CalendarBaseEventDto baseEvent) {
        this.baseEvent = baseEvent;
    }

    @Override
    public int compareTo(final CalendarDto calendarDto) {
        return eventFrom.compareTo(calendarDto.eventFrom) + eventTo.compareTo(calendarDto.eventTo);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarDto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
