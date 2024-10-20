package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.common.base.BaseDto;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.RecurrenceOption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@MemberReferenceDtoConstraint
@CalendarDtoConstraint
public abstract class BaseEventDto extends BaseDto implements BaseEventDtoRequiredFields {
    BaseEventType eventType;
    String name;
    String description;
    String location;
    MemberReference member;
    UUID newMemberId;
    RecurrenceOption recurrenceOption;
    LocalDateTime minTime;
    LocalDateTime maxTime;
    List<CalendarEntry> calendarEntries = new ArrayList<>();
    CalendarEntry newCalendarEntry;

    BaseEventDto(BaseEventType eventType) {
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
    public void setMember(final MemberReference member) {
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
    public List<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(final List<CalendarEntry> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    @Override
    public CalendarEntry getNewCalendarEntry() {
        return newCalendarEntry;
    }

    @Override
    public void setNewCalendarEntry(CalendarEntry newCalendarEntry) {
        this.newCalendarEntry = newCalendarEntry;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
