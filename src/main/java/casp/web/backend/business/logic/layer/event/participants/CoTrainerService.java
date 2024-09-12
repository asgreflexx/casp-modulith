package casp.web.backend.business.logic.layer.event.participants;


import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.types.Course;

import java.util.Set;
import java.util.UUID;

public interface CoTrainerService extends BaseParticipantService<CoTrainer, Course> {
    Set<ParticipantMember> getActiveCoTrainersIfMembersAreActive(UUID baseEventId);
}
