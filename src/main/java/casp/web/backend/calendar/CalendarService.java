package casp.web.backend.calendar;

import jakarta.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

interface CalendarService {
    List<CalendarEntryDto> findCalendarEntriesByFromAndToAndMemberId(OffsetDateTime from, OffsetDateTime to, @Nullable UUID memberId);
}
