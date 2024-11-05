package casp.web.backend.data.access.layer.event.participants;


import casp.web.backend.common.enums.BaseParticipantType;
import casp.web.backend.common.enums.EventResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

abstract class BaseParticipant {
    @NotNull
    protected BaseParticipantType participantType;

    @NotNull
    protected EventResponse response = EventResponse.ACCEPTED;

    protected BaseParticipant(BaseParticipantType participantType) {
        this.participantType = participantType;
    }

    public BaseParticipantType getParticipantType() {
        return participantType;
    }

    public void setParticipantType(BaseParticipantType participantType) {
        this.participantType = participantType;
    }

    public EventResponse getResponse() {
        return response;
    }

    public void setResponse(EventResponse status) {
        response = status;
    }

    @Id
    @NotNull
    public abstract UUID getId();
}
