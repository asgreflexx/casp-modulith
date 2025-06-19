package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.BaseEventRequiredFields;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.calendar.data.participants.BaseParticipant;
import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.reference.MemberReference;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class BaseEventRead<P extends BaseParticipant> extends BaseView implements BaseEventRequiredFields<P> {
    protected BaseEventType eventType;
    protected String name;
    protected String description;
    protected String location;
    protected MemberReference member;
    protected RecurrenceOption recurrenceOption;
    protected LocalDateTime minTime;
    protected LocalDateTime maxTime;
    protected List<CalendarEntry> calendarEntries;
    protected Set<P> participants = new HashSet<>();

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
    public Set<@Valid P> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(final Set<@Valid P> participants) {
        this.participants = participants;
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
