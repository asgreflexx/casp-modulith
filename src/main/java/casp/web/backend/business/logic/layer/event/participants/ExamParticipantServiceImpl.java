package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.event.types.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
    public void replaceParticipants(final Exam exam, final Set<UUID> participantsId) {
        var examParticipants = createExamParticipants(exam, participantsId);
        replaceParticipantsAndSetMetadata(exam, examParticipants);
    }

    @Override
    public Set<ExamParticipant> getActiveParticipantsIfMembersOrDogHasHandlerAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(s -> findDogHasHandler(s.getMemberOrHandlerId())
                        .map(dh -> {
                            s.setDogHasHandler(dh);
                            return s;
                        })
                        .stream())
                .collect(Collectors.toSet());
    }

    private Set<ExamParticipant> createExamParticipants(final Exam exam, final Set<UUID> participantsId) {
        return participantsId
                .stream()
                .flatMap(id ->
                        findDogHasHandler(id).map(dh -> new ExamParticipant(exam, dh)).stream())
                .collect(Collectors.toSet());
    }

    private Optional<DogHasHandler> findDogHasHandler(final UUID dogHasHandlerId) {
        return dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandlerId, EntityStatus.ACTIVE);
    }
}
