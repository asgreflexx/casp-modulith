package casp.web.backend.calendar.data.options;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

// It contains too many interfaces to be a well implemented record.
@Getter
@Setter
@EqualsAndHashCode(of = {"dayOfWeek", "startTime", "endTime"})
@EventOptionTimesConstraint
public class WeeklyOption implements Comparable<WeeklyOption>, EventOptionTimes {

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private static int getCompareValue(LocalTime thisLocalTime, LocalTime otherLocalTime) {
        return thisLocalTime.compareTo(otherLocalTime);
    }
    @Override
    public int compareTo(WeeklyOption other) {
        var compareValue = dayOfWeek.compareTo(other.dayOfWeek);
        if (compareValue == 0) {
            compareValue = getCompareValue(startTime, other.startTime);
        }
        if (compareValue == 0) {
            compareValue = getCompareValue(endTime, other.endTime);
        }
        return compareValue;
    }
}
