package casp.web.backend.calendar;

import jakarta.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BaseEventObserver {
    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);

    List<CalendarEntryDto> getCalendarEntriesBetweenFromAndToOrMemberId(OffsetDateTime from, OffsetDateTime to, @Nullable UUID memberId);
}
