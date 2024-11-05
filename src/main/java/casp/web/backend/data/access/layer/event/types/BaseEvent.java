package casp.web.backend.data.access.layer.event.types;


import casp.web.backend.business.logic.layer.event.types.BaseEventRequiredFields;
import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.RecurrenceOption;
import casp.web.backend.presentation.layer.event.BaseEventWriteRequiredFields;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseEvent extends BaseDocument implements BaseEventWriteRequiredFields, BaseEventRequiredFields {
    protected BaseEventType eventType;

    protected String name;

    protected String description;

    protected String location;

    @NotNull
    @DBRef
    protected MemberReference member;

    protected RecurrenceOption recurrenceOption;

    protected LocalDateTime minTime;

    protected LocalDateTime maxTime;

    protected List<CalendarEntry> calendarEntries = new ArrayList<>();

    protected BaseEvent(BaseEventType eventType) {
        this.eventType = eventType;
    }

    static boolean isMemberNotDeleted(MemberReference member) {
        return EntityStatus.DELETED != member.getEntityStatus();
    }

    static boolean isDogHasHandlerNotDeleted(DogHasHandlerReference dogHasHandler) {
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
    public void setCalendarEntries(List<CalendarEntry> calendarEntries) {
        calendarEntries.sort(CalendarEntry::compareTo);
        this.calendarEntries = calendarEntries;
        minTime = calendarEntries.getFirst().getEntryFrom();
        maxTime = calendarEntries.getLast().getEntryTo();
    }

    public void addCalendarEntry(CalendarEntry calendarEntry) {
        calendarEntries.add(calendarEntry);
        setCalendarEntries(calendarEntries);
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
