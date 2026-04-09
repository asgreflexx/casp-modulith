package casp.web.backend.calendar;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

interface BaseEventService<T extends BaseEventDto<?>> {
    void save(T dto);

    void deleteById(UUID id);

    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);

    Stream<CalendarEntryDto> getCalendarEntriesBetweenFromAndToOrMemberId(LocalDateTime from, LocalDateTime to, @Nullable UUID memberId);

    T getOneById(UUID id);
}
