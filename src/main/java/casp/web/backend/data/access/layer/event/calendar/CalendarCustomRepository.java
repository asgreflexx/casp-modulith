package casp.web.backend.data.access.layer.event.calendar;

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CalendarCustomRepository {
    List<Calendar> findAllBetweenEventFromAndEventToAndEventTypes(LocalDate eventFrom, LocalDate eventTo, @Nullable Set<String> eventTypes);
}
