package casp.web.backend.data.access.layer.event.participants;

import java.util.Set;
import java.util.UUID;

public interface SpaceCustomRepository {
    Set<Space> findAllByMemberId(UUID membersId);

    Set<Space> findAllByDogId(UUID dogsId);
}
