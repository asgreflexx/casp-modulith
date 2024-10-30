package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.validation.CalendarValidation;

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
    public void setEntryFrom(final LocalDateTime entryFrom) {
        this.entryFrom = entryFrom;
    }

    @Override
    public LocalDateTime getEntryTo() {
        return entryTo;
    }

    @Override
    public void setEntryTo(final LocalDateTime entryTo) {
        this.entryTo = entryTo;
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
