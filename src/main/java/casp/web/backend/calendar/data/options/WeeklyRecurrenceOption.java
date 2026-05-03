package casp.web.backend.calendar.data.options;

import casp.web.backend.calendar.data.BaseRecurrenceOptionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RecurrenceConstraint(message = "The start recurrence date must be at least 6 days before the end recurrence date", plusDays = 6)
public class WeeklyRecurrenceOption extends RecurrenceOption {
    @Valid
    @NotEmpty
    private List<WeeklyOption> occurrences = new ArrayList<>();

    public WeeklyRecurrenceOption() {
        super(BaseRecurrenceOptionType.WEEKLY);
    }
}
