package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.enumerations.EventResponse;
import casp.web.backend.data.access.layer.event.TypesRegex;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public abstract class BaseParticipantDto {
    protected UUID id = UUID.randomUUID();
    @NotNull
    @Pattern(regexp = TypesRegex.BASE_PARTICIPANT_TYPES_REGEX)
    protected String participantType;
    @NotNull
    protected UUID memberOrHandlerId;
    @NotNull
    protected EventResponse response = EventResponse.ACCEPTED;

    BaseParticipantDto(final String participantType) {
        this.participantType = participantType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getParticipantType() {
        return participantType;
    }

    public void setParticipantType(final String participantType) {
        this.participantType = participantType;
    }

    public UUID getMemberOrHandlerId() {
        return memberOrHandlerId;
    }

    public void setMemberOrHandlerId(final UUID memberOrHandlerId) {
        this.memberOrHandlerId = memberOrHandlerId;
    }

    public EventResponse getResponse() {
        return response;
    }

    public void setResponse(final EventResponse response) {
        this.response = response;
    }
}
