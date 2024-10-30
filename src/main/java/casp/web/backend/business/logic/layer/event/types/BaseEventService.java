package casp.web.backend.business.logic.layer.event.types;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

interface BaseEventService<T extends BaseEventDto> {
    void save(T dto);

    void deleteById(UUID id);

    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);

    Set<CalendarEntryDto> getCalendarEntriesBetweenFromAndTo(LocalDateTime from, LocalDateTime to);

    T getOneByIdAndCalendarEntryId(UUID id, UUID calendarEntryId);

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    void migrateDataToV2();
}
