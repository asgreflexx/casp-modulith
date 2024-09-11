package casp.web.backend.business.logic.layer.events.participants;


import casp.web.backend.data.access.layer.documents.event.participants.Space;
import casp.web.backend.data.access.layer.documents.event.types.Course;

import java.util.Set;
import java.util.UUID;

public interface SpaceService extends BaseParticipantService<Space, Course> {
    Space saveParticipant(Space space);

    Set<Space> getSpacesByDogHasHandlersId(Set<UUID> dogHasHandlersId);
}
