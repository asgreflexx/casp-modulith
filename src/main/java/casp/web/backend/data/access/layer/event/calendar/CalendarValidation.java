package casp.web.backend.data.access.layer.event.calendar;

import java.time.LocalDateTime;

public interface CalendarValidation {
    LocalDateTime getEventFrom();

    LocalDateTime getEventTo();
}
