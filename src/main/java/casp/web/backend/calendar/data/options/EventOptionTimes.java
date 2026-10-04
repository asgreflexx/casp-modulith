package casp.web.backend.calendar.data.options;

import java.time.LocalTime;

/**
 * This interface is used because the validation at first.
 */
public interface EventOptionTimes {

    LocalTime getStartTime();

    LocalTime getEndTime();
}
