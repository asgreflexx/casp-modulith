package casp.web.backend.presentation.layer.dtos.event.types;


import casp.web.backend.data.access.layer.event.TypesRegex;
import casp.web.backend.data.access.layer.event.options.BaseEventOption;
import casp.web.backend.presentation.layer.dtos.event.calendar.CalendarDto;
import casp.web.backend.presentation.layer.dtos.member.SimpleMemberDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class BaseEventDto<P> {
    protected UUID id;
    protected long version;
    protected LocalDateTime created;
    protected LocalDateTime modified;
    @NotNull
    @Pattern(regexp = TypesRegex.BASE_EVENT_TYPES_REGEX)
    protected String eventType;
    protected String name;
    protected String description;
    protected SimpleMemberDto member;
    @Valid
    protected BaseEventOption option;
    @NotEmpty
    @Valid
    protected List<CalendarDto> calendarEntries = new ArrayList<>();
    @Valid
    protected Set<P> participants = new HashSet<>();

    BaseEventDto(final String eventType) {
        this.eventType = eventType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(final LocalDateTime modified) {
        this.modified = modified;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public SimpleMemberDto getMember() {
        return member;
    }

    public void setMember(final SimpleMemberDto member) {
        this.member = member;
    }

    public BaseEventOption getOption() {
        return option;
    }

    public void setOption(final BaseEventOption option) {
        this.option = option;
    }

    public List<CalendarDto> getCalendarEntries() {
        return calendarEntries;
    }

    public void setCalendarEntries(final List<CalendarDto> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    public Set<P> getParticipants() {
        return participants;
    }

    public void setParticipants(final Set<P> participants) {
        this.participants = participants;
    }
}
