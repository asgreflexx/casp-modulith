package casp.web.backend.data.access.layer.documents.event.options;


import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

// It contains too many interfaces to be a well implemented record.
@EventOptionTimesConstraint
public class WeeklyEventOptionRecurrence implements Comparable<WeeklyEventOptionRecurrence>, EventOptionTimes {

    @Id
    protected UUID id = UUID.randomUUID();

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
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
    public int compareTo(WeeklyEventOptionRecurrence o) {
        int compareValue = getDayOfWeek().getValue() - o.getDayOfWeek().getValue();
        if (compareValue == 0) {
            compareValue = getCompareValue(getStartTime(), o.getStartTime());
        }
        if (compareValue == 0) {
            compareValue = getCompareValue(getEndTime(), o.getEndTime());
        }
        return compareValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof WeeklyEventOptionRecurrence that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private static int getCompareValue(LocalTime thisLocalTime, LocalTime otherLocalTime) {
        long value = thisLocalTime.toNanoOfDay() - otherLocalTime.toNanoOfDay();
        if (value < 0) {
            return -1;
        } else if (value > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WeeklyEventOptionRecurrence.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("dayOfWeek=" + dayOfWeek)
                .add("startTime=" + startTime)
                .add("endTime=" + endTime)
                .toString();
    }
}
