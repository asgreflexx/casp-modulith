package casp.web.backend.data.access.layer.event.types;


import casp.web.backend.business.logic.layer.event.types.BaseEventRequiredFields;
import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.BaseEventOption;
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

    BaseEventOption baseEventOption;

    LocalDateTime minLocalDateTime;

    LocalDateTime maxLocalDateTime;

    List<CalendarEntry> calendarEntries = new ArrayList<>();

    BaseEvent(BaseEventType eventType) {
        this.eventType = eventType;
    }

    static boolean isMemberActive(final MemberReference member) {
        return EntityStatus.ACTIVE == member.getEntityStatus();
    }

    static boolean isDogHasHandlerActive(final DogHasHandlerReference dogHasHandler) {
        return dogHasHandler.isActive();
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
    public BaseEventOption getBaseEventOption() {
        return baseEventOption;
    }

    @Override
    public void setBaseEventOption(BaseEventOption baseEventOption) {
        this.baseEventOption = baseEventOption;
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
    public LocalDateTime getMinLocalDateTime() {
        return minLocalDateTime;
    }

    @Override
    public void setMinLocalDateTime(LocalDateTime minLocalDateTime) {
        this.minLocalDateTime = minLocalDateTime;
    }

    @Override
    public LocalDateTime getMaxLocalDateTime() {
        return maxLocalDateTime;
    }

    @Override
    public void setMaxLocalDateTime(LocalDateTime maxLocalDateTime) {
        this.maxLocalDateTime = maxLocalDateTime;
    }

    @Override
    public List<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(final List<CalendarEntry> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }
}
