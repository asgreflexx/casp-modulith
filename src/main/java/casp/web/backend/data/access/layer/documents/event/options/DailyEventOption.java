package casp.web.backend.data.access.layer.documents.event.options;


import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.StringJoiner;

@EventOptionTimesConstraint
public class DailyEventOption extends BaseEventOption implements EventOptionTimes {

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

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
    public String toString() {
        return new StringJoiner(", ", DailyEventOption.class.getSimpleName() + "[", "]")
                .add("startTime=" + startTime)
                .add("endTime=" + endTime)
                .add("startRecurrence=" + startRecurrence)
                .add("endRecurrence=" + endRecurrence)
                .add("repeatEvery=" + repeatEvery)
                .toString();
    }
}
