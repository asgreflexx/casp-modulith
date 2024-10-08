package casp.web.backend.presentation.layer.dtos.event.calendar;

import casp.web.backend.presentation.layer.dtos.member.SimpleMemberDto;

import java.util.UUID;

public class CalendarBaseEventDto {
    private UUID id;
    private String eventType;
    private String name;
    private String description;
    private SimpleMemberDto member;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
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
}
