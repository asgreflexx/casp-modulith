package casp.web.backend.calendar;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BaseEventObserver {
    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);

    List<CalendarEntryDto> getCalendarEntriesBetweenFromAndToOrMemberId(LocalDateTime from, LocalDateTime to, @Nullable UUID memberId);
}
