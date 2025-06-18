package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.reference.MemberReference;

import java.time.LocalDateTime;
import java.util.List;

abstract class BaseEventRead extends BaseView implements BaseEventReadRequiredFields {
    protected BaseEventType eventType;
    protected String name;
    protected String description;
    protected String location;
    protected MemberReference member;
    protected RecurrenceOption recurrenceOption;
    protected LocalDateTime minTime;
    protected LocalDateTime maxTime;
    protected List<CalendarEntry> calendarEntries;

    BaseEventRead(final BaseEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public BaseEventType getEventType() {
        return this.eventType;
    }

    @Override
    public void setEventType(final BaseEventType eventType) {
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
    public RecurrenceOption getRecurrenceOption() {
        return recurrenceOption;
    }

    @Override
    public void setRecurrenceOption(RecurrenceOption recurrenceOption) {
        this.recurrenceOption = recurrenceOption;
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
