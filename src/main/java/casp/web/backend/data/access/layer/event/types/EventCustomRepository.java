package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;

import java.util.Set;
import java.util.UUID;

public interface EventCustomRepository {
    Set<Event> findAllByMemberIdAndNotDeleted(UUID memberId);

    Set<Event> findAllByMemberIdAndStatus(UUID memberId, EntityStatus entityStatus);
}
