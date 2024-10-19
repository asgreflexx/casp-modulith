package casp.web.backend.data.access.layer.event.types;


import casp.web.backend.business.logic.layer.event.types.BaseEventRequiredFields;
import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.RecurrenceOption;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseEvent extends BaseDocument implements BaseEventRequiredFields {
    BaseEventType eventType;

    String name;

    String description;

    String location;

    @DBRef
    MemberReference member;

    RecurrenceOption recurrenceOption;

    LocalDateTime minTime;

    LocalDateTime maxTime;

    List<CalendarEntry> calendarEntries = new ArrayList<>();

    BaseEvent(BaseEventType eventType) {
        this.eventType = eventType;
    }

    static boolean isMemberNotDeleted(final MemberReference member) {
        return EntityStatus.DELETED != member.getEntityStatus();
    }

    static boolean isDogHasHandlerNotDeleted(final DogHasHandlerReference dogHasHandler) {
        return EntityStatus.DELETED != dogHasHandler.getEntityStatus()
                && isMemberNotDeleted(dogHasHandler.getMember())
                && EntityStatus.DELETED != dogHasHandler.getDog().getEntityStatus();
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
        calendarEntries.sort(CalendarEntry::compareTo);
        this.calendarEntries = calendarEntries;
        this.minTime = calendarEntries.getFirst().getEntryFrom();
        this.maxTime = calendarEntries.getLast().getEntryTo();
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
