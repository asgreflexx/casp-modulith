package casp.web.backend.calendar.data;

import jakarta.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CalendarRepository {
    List<CalendarEntryProjection> findCalendarEntriesByFromAndToAndMemberId(OffsetDateTime from,
                                                                            OffsetDateTime to,
                                                                            @Nullable UUID memberId);
}
