package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CalendarCustomRepository {
    List<Calendar> findAllBetweenEventFromAndEventToAndEventTypes(LocalDate eventFrom, LocalDate eventTo, @Nullable Set<String> eventTypes);

    List<Calendar> findAllByBaseEventId(UUID baseEventId);
}
