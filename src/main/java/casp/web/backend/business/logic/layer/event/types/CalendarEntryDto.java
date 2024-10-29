package casp.web.backend.business.logic.layer.event.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CalendarEntryDto extends BaseEventDto implements Comparable<CalendarEntryDto> {
    private UUID calendarEntryId;

    public CalendarEntryDto() {
        super(null);
    }

    @JsonProperty("from")
    @Override
    public LocalDateTime getMinTime() {
        return super.getMinTime();
    }

    @JsonProperty("to")
    @Override
    public LocalDateTime getMaxTime() {
        return super.getMaxTime();
    }

    public UUID getCalendarEntryId() {
        return calendarEntryId;
    }

    public void setCalendarEntryId(UUID calendarEntryId) {
        this.calendarEntryId = calendarEntryId;
    }

    @Override
    public int compareTo(final CalendarEntryDto calendarEntryDto) {
        return this.minTime.compareTo(calendarEntryDto.getMinTime()) + this.maxTime.compareTo(calendarEntryDto.getMaxTime());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarEntryDto that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(calendarEntryId, that.calendarEntryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calendarEntryId);
    }
}
