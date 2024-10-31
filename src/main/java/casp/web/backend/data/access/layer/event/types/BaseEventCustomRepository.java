package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface BaseEventCustomRepository<T extends BaseEvent> {
    Set<T> findAllByMemberIdAndNotDeleted(UUID memberId);

    Set<T> findAllByMemberIdAndStatus(UUID memberId, EntityStatus status);

    Stream<T> findAllBetweenFromAndTo(LocalDateTime from, LocalDateTime to);
}
