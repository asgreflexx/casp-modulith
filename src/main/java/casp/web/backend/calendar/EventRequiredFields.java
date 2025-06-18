package casp.web.backend.calendar;

import casp.web.backend.calendar.data.participants.EventParticipant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public interface EventRequiredFields {
    @NotNull
    Set<@Valid EventParticipant> getParticipants();

    void setParticipants(@NotNull Set<@Valid EventParticipant> participants);
}
