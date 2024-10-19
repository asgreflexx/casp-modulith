package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.base.BaseDto;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.RecurrenceOption;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@CoTrainersDtoConstraint
@SpacesDtoConstraint
@MemberReferenceDtoConstraint
@CalendarDtoConstraint
public class CourseDto extends BaseDto implements CourseRequiredFields, CourseDtoRequiredFields {
    private int spaceLimit;
    private Set<CoTrainer> coTrainers = new HashSet<>();
    private Set<UUID> newCoTrainers = new HashSet<>();
    private Set<Space> spaces = new HashSet<>();
    private Set<UUID> newSpaces = new HashSet<>();
    private int spaceListSize;
    private String name;
    private String description;
    private String location;
    private MemberReference member;
    private UUID newMemberId;
    private RecurrenceOption recurrenceOption;
    private BaseEventType eventType;
    private LocalDateTime minLocalDateTime;
    private LocalDateTime maxLocalDateTime;
    private List<CalendarEntry> calendarEntries;
    private CalendarEntry newCalendarEntry;

    @Override
    public int getSpaceLimit() {
        return spaceLimit;
    }

    @Override
    public void setSpaceLimit(final int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }

    @Override
    public Set<CoTrainer> getCoTrainers() {
        return coTrainers;
    }

    @Override
    public void setCoTrainers(final Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
    }

    @Override
    public Set<UUID> getNewCoTrainers() {
        return newCoTrainers;
    }

    @Override
    public void setNewCoTrainers(final Set<UUID> newCoTrainers) {
        this.newCoTrainers = newCoTrainers;
    }

    @Override
    public Set<Space> getSpaces() {
        return spaces;
    }

    @Override
    public void setSpaces(final Set<Space> spaces) {
        this.spaces = spaces;
    }

    @Override
    public Set<UUID> getNewSpaces() {
        return newSpaces;
    }

    @Override
    public void setNewSpaces(final Set<UUID> newSpaces) {
        this.newSpaces = newSpaces;
    }

    @Override
    public int getSpaceListSize() {
        return spaceListSize;
    }

    @Override
    public void setSpaceListSize(final int spaceListSize) {
        this.spaceListSize = spaceListSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;

    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(final String location) {
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
    public void setNewMemberId(final UUID newMemberId) {
        this.newMemberId = newMemberId;
    }

    @Override
    public RecurrenceOption getRecurrenceOption() {
        return recurrenceOption;
    }

    @Override
    public void setRecurrenceOption(final RecurrenceOption recurrenceOption) {
        this.recurrenceOption = recurrenceOption;
    }

    @Override
    public BaseEventType getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(final BaseEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public LocalDateTime getMinTime() {
        return minLocalDateTime;
    }

    @Override
    public void setMinTime(final LocalDateTime minTime) {
        this.minLocalDateTime = minTime;
    }

    @Override
    public LocalDateTime getMaxTime() {
        return maxLocalDateTime;
    }

    @Override
    public void setMaxTime(final LocalDateTime maxTime) {
        this.maxLocalDateTime = maxTime;
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
    public void setNewCalendarEntry(final CalendarEntry newCalendarEntry) {
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
