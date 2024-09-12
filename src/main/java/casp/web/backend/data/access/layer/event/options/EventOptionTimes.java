package casp.web.backend.data.access.layer.event.options;

import java.time.LocalTime;

/**
 * This interface is used because the validation at first.
 */
public interface EventOptionTimes {

    LocalTime getStartTime();

    LocalTime getEndTime();
}
