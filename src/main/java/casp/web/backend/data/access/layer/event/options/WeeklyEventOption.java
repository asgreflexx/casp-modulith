package casp.web.backend.data.access.layer.event.options;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class WeeklyEventOption extends BaseEventOption {

    @Valid
    @NotEmpty
    private List<WeeklyEventOptionRecurrence> occurrences = new ArrayList<>();

    public List<WeeklyEventOptionRecurrence> getOccurrences() {
        occurrences.sort(WeeklyEventOptionRecurrence::compareTo);
        return occurrences;
    }

    public void setOccurrences(List<WeeklyEventOptionRecurrence> occurrences) {
        this.occurrences = occurrences;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WeeklyEventOption.class.getSimpleName() + "[", "]")
                .add("occurrences=" + occurrences)
                .add("startRecurrence=" + startRecurrence)
                .add("endRecurrence=" + endRecurrence)
                .add("repeatEvery=" + repeatEvery)
                .toString();
    }
}
