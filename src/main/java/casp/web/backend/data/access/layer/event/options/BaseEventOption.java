package casp.web.backend.data.access.layer.event.options;

import casp.web.backend.data.access.layer.event.types.BaseEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * This class isn't abstract for the same reason, like {@link BaseEvent}
 * Would be better to use baseEvent as id, but is giving too many errors
 * https://stackoverflow.com/questions/16246675/hibernate-error-a-different-object-with-the-same-identifier-value-was-already-a
 */
@BaseEventOptionRecurrencesConstraint
public abstract class BaseEventOption {
    @NotBlank
    protected String optionType;

    @NotNull
    protected LocalDate startRecurrence;

    @NotNull
    protected LocalDate endRecurrence;

    @Positive
    protected int repeatEvery = 1;

    BaseEventOption(final String optionType) {
        this.optionType = optionType;
    }

    public LocalDate getStartRecurrence() {
        return startRecurrence;
    }

    public void setStartRecurrence(LocalDate startRecurrence) {
        this.startRecurrence = startRecurrence;
    }

    public LocalDate getEndRecurrence() {
        return endRecurrence;
    }

    public void setEndRecurrence(LocalDate endRecurrence) {
        this.endRecurrence = endRecurrence;
    }

    public int getRepeatEvery() {
        return repeatEvery;
    }

    public void setRepeatEvery(int repeatEvery) {
        this.repeatEvery = repeatEvery;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(final String optionType) {
        this.optionType = optionType;
    }
}
