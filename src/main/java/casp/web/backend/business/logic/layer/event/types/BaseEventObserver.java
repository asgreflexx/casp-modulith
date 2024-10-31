package casp.web.backend.business.logic.layer.event.types;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public interface BaseEventObserver {
    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);

    Stream<CalendarEntryDto> getCalendarEntriesBetweenFromAndTo(LocalDateTime from, LocalDateTime to);
}
