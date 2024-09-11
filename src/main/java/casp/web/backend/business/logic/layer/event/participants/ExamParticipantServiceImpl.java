package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import casp.web.backend.data.access.layer.repositories.DogHasHandlerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class ExamParticipantServiceImpl extends BaseParticipantServiceImpl<ExamParticipant, Exam> implements ExamParticipantService {
    private final DogHasHandlerRepository dogHasHandlerRepository;

    @Autowired
    ExamParticipantServiceImpl(final BaseParticipantRepository baseParticipantRepository, final DogHasHandlerRepository dogHasHandlerRepository) {
        super(baseParticipantRepository, ExamParticipant.PARTICIPANT_TYPE);
        this.dogHasHandlerRepository = dogHasHandlerRepository;
    }

    @Override
    public Set<ParticipantDogHasHandler> getActiveExamParticipantsIfDogHasHandlersAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(s -> dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(s.getMemberOrHandlerId(), EntityStatus.ACTIVE)
                        .map(dh -> new ParticipantDogHasHandler(s, dh))
                        .stream())
                .collect(Collectors.toSet());
    }
}
