package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
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
    public Set<Space> getActiveParticipantsIfMembersOrDogHasHandlerAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(s -> dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(s.getMemberOrHandlerId(), EntityStatus.ACTIVE)
                        .map(dh -> {
                            s.setDogHasHandler(dh);
                            return s;
                        })
                        .stream())
                .collect(Collectors.toSet());
    }
}
