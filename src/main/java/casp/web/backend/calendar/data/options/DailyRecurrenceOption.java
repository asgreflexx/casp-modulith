package casp.web.backend.calendar.data.options;

import casp.web.backend.calendar.data.BaseRecurrenceOptionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
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
}
