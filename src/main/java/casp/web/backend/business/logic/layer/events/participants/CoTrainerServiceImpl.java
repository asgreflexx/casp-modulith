package casp.web.backend.business.logic.layer.events.participants;

import casp.web.backend.data.access.layer.documents.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
class CoTrainerServiceImpl extends BaseParticipantServiceImpl<CoTrainer, Course> implements CoTrainerService {

    @Autowired
    CoTrainerServiceImpl(final BaseParticipantRepository baseParticipantRepository) {
        super(baseParticipantRepository, CoTrainer.PARTICIPANT_TYPE);
    }
}
