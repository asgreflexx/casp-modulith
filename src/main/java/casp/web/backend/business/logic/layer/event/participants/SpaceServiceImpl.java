package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participants.Space;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import casp.web.backend.data.access.layer.repositories.DogHasHandlerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
class SpaceServiceImpl extends BaseParticipantServiceImpl<Space, Course> implements SpaceService {
    private final DogHasHandlerRepository dogHasHandlerRepository;

    @Autowired
    SpaceServiceImpl(final BaseParticipantRepository baseParticipantRepository, final DogHasHandlerRepository dogHasHandlerRepository) {
        super(baseParticipantRepository, Space.PARTICIPANT_TYPE);
        this.dogHasHandlerRepository = dogHasHandlerRepository;
    }

    @Override
    public Space saveParticipant(final Space space) {
        return baseParticipantRepository.save(space);
    }

    @Override
    public Set<Space> getSpacesByDogHasHandlersId(final Set<UUID> dogHasHandlersId) {
        return baseParticipantRepository.findAllByMemberOrHandlerIdIn(dogHasHandlersId, participantType);
    }

    @Override
    public Set<ParticipantDogHasHandler> getActiveSpacesIfDogHasHandlersAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(s -> dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(s.getMemberOrHandlerId(), EntityStatus.ACTIVE)
                        .map(dh -> new ParticipantDogHasHandler(s, dh))
                        .stream())
                .collect(Collectors.toSet());
    }
}
