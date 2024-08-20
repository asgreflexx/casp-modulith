package casp.web.backend.data.access.layer.documents.event.options;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

public class WeeklyEventOption extends BaseEventOption {

    @Valid
    @NotEmpty
    private Set<WeeklyEventOptionRecurrence> occurrences = new TreeSet<>();

    public Set<WeeklyEventOptionRecurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Set<WeeklyEventOptionRecurrence> occurrences) {
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
