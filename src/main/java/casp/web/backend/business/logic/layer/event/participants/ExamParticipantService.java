package casp.web.backend.business.logic.layer.event.participants;


import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Exam;

import java.util.Set;
import java.util.UUID;

public interface ExamParticipantService extends BaseParticipantService<ExamParticipant, Exam> {
    Set<ParticipantDogHasHandler> getActiveExamParticipantsIfDogHasHandlersAreActive(UUID baseEventId);
}
