package casp.web.backend.data.access.layer.event.options;


import casp.web.backend.common.enums.BaseRecurrenceOptionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

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
        return occurrences;
    }

    public void setOccurrences(List<WeeklyOption> occurrences) {
        this.occurrences = occurrences;
    }
}
