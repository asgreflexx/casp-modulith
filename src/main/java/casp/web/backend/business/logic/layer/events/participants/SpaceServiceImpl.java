package casp.web.backend.business.logic.layer.events.participants;

import casp.web.backend.data.access.layer.documents.event.participant.Space;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;


@Service
class SpaceServiceImpl extends BaseParticipantServiceImpl<Space, Course> implements SpaceService {

    @Autowired
    SpaceServiceImpl(final BaseParticipantRepository baseParticipantRepository) {
        super(baseParticipantRepository, Space.PARTICIPANT_TYPE);
    }

    @Override
    public Space saveParticipant(final Space space) {
        return baseParticipantRepository.save(space);
    }

    @Override
    public Set<Space> getSpacesByDogHasHandlersId(final Set<UUID> dogHasHandlersId) {
        return baseParticipantRepository.findAllByMemberOrHandlerIdIn(dogHasHandlersId, participantType);
    }
}
