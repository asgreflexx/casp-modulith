package casp.web.backend.calendar.data.options;

import casp.web.backend.calendar.options.BaseRecurrenceOptionType;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@EventOptionTimesConstraint
@RecurrenceConstraint
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

    @Override
    public LocalTime getEndTime() {
        return endTime;
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
