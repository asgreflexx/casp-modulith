package casp.web.backend.data.access.layer.event.options;


import casp.web.backend.common.enums.BaseRecurrenceOptionType;
import casp.web.backend.common.validation.EventOptionTimes;
import casp.web.backend.common.validation.EventOptionTimesConstraint;
import jakarta.validation.constraints.NotNull;

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
}
