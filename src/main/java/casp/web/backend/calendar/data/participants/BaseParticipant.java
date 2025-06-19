package casp.web.backend.calendar.data.participants;


import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseParticipant {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseParticipant that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
