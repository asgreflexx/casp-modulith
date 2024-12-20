package casp.web.backend.calendar.data.options;


import casp.web.backend.calendar.options.BaseRecurrenceOptionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

@EventOptionTimesConstraint
public class DailyRecurrenceOption extends RecurrenceOption implements EventOptionTimes {
    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    public DailyRecurrenceOption() {
        super(BaseRecurrenceOptionType.DAILY);
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
    public LocalDateTime min() {
        return LocalDateTime.of(startRecurrence, startTime);
    }

    @Override
    public LocalDateTime max() {
        return LocalDateTime.of(endRecurrence, endTime);
    }
}
