package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarValidation;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CalendarEntryDto extends BaseEventDto implements Comparable<CalendarEntryDto>, CalendarValidation {
    private UUID calendarEntryId;
    private LocalDateTime entryFrom;
    private LocalDateTime entryTo;

    public CalendarEntryDto() {
        super(null);
    }

    public UUID getCalendarEntryId() {
        return calendarEntryId;
    }

    public void setCalendarEntryId(UUID calendarEntryId) {
        this.calendarEntryId = calendarEntryId;
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
        return minTime.compareTo(calendarEntryDto.getMinTime()) + maxTime.compareTo(calendarEntryDto.getMaxTime());
    }

    @Override
    public boolean equals(Object o) {
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
