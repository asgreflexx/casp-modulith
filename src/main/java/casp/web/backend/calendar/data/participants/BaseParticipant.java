package casp.web.backend.calendar.data.participants;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseParticipant {
    @NotNull
    protected BaseParticipantType participantType;

    @NotNull
    protected EventResponse response = EventResponse.ACCEPTED;

    protected BaseParticipant(BaseParticipantType participantType) {
        this.participantType = participantType;
    }

    @EqualsAndHashCode.Include
    @Id
    @NotNull
    public abstract UUID getId();
}
