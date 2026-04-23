package casp.web.backend.calendar.data;

import casp.web.backend.common.enums.EntityStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface BaseEventCustomRepository<T extends BaseEvent<?>> {
    Set<T> findAllByMemberIdAndNotDeleted(UUID memberId);

    Set<T> findAllByMemberIdAndStatus(UUID memberId, EntityStatus status);

    Stream<T> findAllBetweenFromAndToOrMemberId(LocalDateTime from, LocalDateTime to, @Nullable UUID memberId);

    @Deprecated(forRemoval = true, since = "2026-04-23")
    boolean migrateLocaDateTimeToOffsetDateTime();
}
