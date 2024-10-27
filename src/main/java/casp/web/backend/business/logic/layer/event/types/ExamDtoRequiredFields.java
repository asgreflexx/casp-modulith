package casp.web.backend.business.logic.layer.event.types;

import java.util.Set;
import java.util.UUID;

public interface ExamDtoRequiredFields {
    Set<UUID> getNewParticipants();

    void setNewParticipants(Set<UUID> newParticipants);
}
