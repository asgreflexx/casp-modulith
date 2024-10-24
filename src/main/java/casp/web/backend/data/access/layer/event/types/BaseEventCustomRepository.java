package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;

import java.util.Set;
import java.util.UUID;

public interface BaseEventCustomRepository<T extends BaseEvent> {
    Set<T> findAllByMemberIdAndNotDeleted(UUID memberId);

    Set<T> findAllByMemberIdAndStatus(UUID memberId, EntityStatus status);
}
