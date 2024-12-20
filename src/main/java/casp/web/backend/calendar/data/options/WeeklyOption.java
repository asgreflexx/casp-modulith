package casp.web.backend.calendar.data.options;


import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

// It contains too many interfaces to be a well implemented record.
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeeklyOption that)) return false;
        return dayOfWeek == that.dayOfWeek && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }
}
