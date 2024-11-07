package casp.web.backend.business.logic.layer.event.types;

import java.util.Set;
import java.util.UUID;

@ExamParticipantsDtoConstraint
public interface ExamDtoRequiredFields extends BaseEventDtoRequiredFields, ExamRequiredFields {
    Set<UUID> getNewParticipants();

    void setNewParticipants(Set<UUID> newParticipants);
}
