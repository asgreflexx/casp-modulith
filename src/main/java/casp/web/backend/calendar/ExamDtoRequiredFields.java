package casp.web.backend.calendar;

import java.util.Set;
import java.util.UUID;

@ExamParticipantsDtoConstraint
public interface ExamDtoRequiredFields extends ExamRequiredFields {
    Set<UUID> getNewParticipants();

    void setNewParticipants(Set<UUID> newParticipants);
}
