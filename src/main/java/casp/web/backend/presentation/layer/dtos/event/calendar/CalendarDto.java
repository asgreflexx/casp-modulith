package casp.web.backend.presentation.layer.dtos.event.calendar;


import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@CalendarFromToConstraint
public class CalendarDto implements Comparable<CalendarDto> {
    private UUID id = UUID.randomUUID();
    @NotNull
    private LocalDateTime eventFrom;
    @NotNull
    private LocalDateTime eventTo;
    private String location;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public LocalDateTime getEventFrom() {
        return eventFrom;
    }

    public void setEventFrom(final LocalDateTime eventFrom) {
        this.eventFrom = eventFrom;
    }

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
