package casp.web.backend.calendar;


import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.common.base.BaseDto;
import casp.web.backend.common.reference.MemberReference;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BaseEventDto extends BaseDto implements BaseEventDtoWriteRequiredFields, BaseEventRequiredFields {
    protected BaseEventType eventType;
    protected String name;
    protected String description;
    protected String location;
    protected MemberReference member;
    protected UUID newMemberId;
    protected RecurrenceOption recurrenceOption;
    protected LocalDateTime minTime;
    protected LocalDateTime maxTime;
    protected NewCalendarEntryDto newCalendarEntry;
    protected List<CalendarEntry> calendarEntries = new ArrayList<>();

    protected BaseEventDto(BaseEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public MemberReference getMember() {
        return member;
    }

    @Override
    public void setMember(MemberReference member) {
        this.member = member;
    }

    @Override
    public UUID getNewMemberId() {
        return newMemberId;
    }

    @Override
    public void setNewMemberId(UUID newMemberId) {
        this.newMemberId = newMemberId;
    }

    @Override
    public RecurrenceOption getRecurrenceOption() {
        return recurrenceOption;
    }

    @Override
    public void setRecurrenceOption(RecurrenceOption recurrenceOption) {
        this.recurrenceOption = recurrenceOption;
    }

    @Override
    public BaseEventType getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(BaseEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public LocalDateTime getMinTime() {
        return minTime;
    }

    @Override
    public void setMinTime(LocalDateTime minTime) {
        this.minTime = minTime;
    }

    @Override
    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    @Override
    public void setMaxTime(LocalDateTime maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public @Valid NewCalendarEntryDto getNewCalendarEntry() {
        return newCalendarEntry;
    }

    @Override
    public void setNewCalendarEntry(@Valid NewCalendarEntryDto newCalendarEntry) {
        this.newCalendarEntry = newCalendarEntry;
    }

    @Override
    public List<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(List<CalendarEntry> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
