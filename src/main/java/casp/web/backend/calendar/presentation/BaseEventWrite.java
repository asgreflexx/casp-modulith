package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.BaseEventDtoWriteRequiredFields;
import casp.web.backend.calendar.NewCalendarEntryDto;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.reference.MemberReference;

import java.util.UUID;

abstract class BaseEventWrite extends BaseView implements BaseEventDtoWriteRequiredFields {
    protected String name;
    protected String description;
    protected String location;
    protected MemberReference member;
    protected RecurrenceOption recurrenceOption;
    protected UUID newMemberId;
    protected NewCalendarEntryDto newCalendarEntry;

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
    public UUID getNewMemberId() {
        return newMemberId;
    }

    @Override
    public void setNewMemberId(UUID newMemberId) {
        this.newMemberId = newMemberId;
    }

    @Override
    public NewCalendarEntryDto getNewCalendarEntry() {
        return newCalendarEntry;
    }

    @Override
    public void setNewCalendarEntry(NewCalendarEntryDto newCalendarEntry) {
        this.newCalendarEntry = newCalendarEntry;
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
