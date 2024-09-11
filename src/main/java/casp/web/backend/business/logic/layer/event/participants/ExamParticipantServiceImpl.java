package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ExamParticipantServiceImpl extends BaseParticipantServiceImpl<ExamParticipant, Exam> implements ExamParticipantService {

    @Autowired
    ExamParticipantServiceImpl(final BaseParticipantRepository baseParticipantRepository) {
        super(baseParticipantRepository, ExamParticipant.PARTICIPANT_TYPE);
    }
}
