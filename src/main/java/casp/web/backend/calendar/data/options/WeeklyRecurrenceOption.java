package casp.web.backend.calendar.data.options;


import casp.web.backend.calendar.options.BaseRecurrenceOptionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WeeklyRecurrenceOption extends RecurrenceOption {
    @Valid
    @NotEmpty
    private List<WeeklyOption> occurrences = new ArrayList<>();

    public WeeklyRecurrenceOption() {
        super(BaseRecurrenceOptionType.WEEKLY);
    }

    public List<WeeklyOption> getOccurrences() {
        return occurrences.stream().sorted().toList();
    }

    public void setOccurrences(List<WeeklyOption> occurrences) {
        this.occurrences = occurrences;
    }

    @Override
    public LocalDateTime min() {
        var first = getOccurrences().getFirst();
        return LocalDateTime.of(startRecurrence, first.getStartTime());
    }

    @Override
    public LocalDateTime max() {
        var last = getOccurrences().getLast();
        return LocalDateTime.of(endRecurrence, last.getEndTime());
    }
}
