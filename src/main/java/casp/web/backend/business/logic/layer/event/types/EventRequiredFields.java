package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public interface EventRequiredFields {
    @NotNull
    @Valid
    Set<EventParticipant> getParticipants();

    void setParticipants(@NotNull @Valid Set<EventParticipant> participants);
}
