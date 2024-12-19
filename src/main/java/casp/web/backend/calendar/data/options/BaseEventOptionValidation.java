package casp.web.backend.calendar.data.options;

import java.time.LocalDate;

public interface BaseEventOptionValidation {
    LocalDate getStartRecurrence();

    LocalDate getEndRecurrence();
}
