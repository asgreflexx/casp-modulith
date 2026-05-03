package casp.web.backend.calendar.data;

import casp.web.backend.common.enums.EntityStatus;

import java.util.Set;
import java.util.UUID;

public interface BaseEventCustomRepository<T extends BaseEvent<?>> {
    Set<T> findAllByMemberIdAndNotDeleted(UUID memberId);

    Set<T> findAllByMemberIdAndStatus(UUID memberId, EntityStatus status);

    @Deprecated(forRemoval = true, since = "2026-04-23")
    boolean migrateLocaDateTimeToOffsetDateTime();
}
