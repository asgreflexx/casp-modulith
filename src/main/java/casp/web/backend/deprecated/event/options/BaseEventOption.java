package casp.web.backend.deprecated.event.options;

import casp.web.backend.data.access.layer.event.options.RecurrenceOption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;


/**
 * @deprecated use {@link RecurrenceOption} instead. It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public abstract class BaseEventOption {
    @NotBlank
    protected String optionType;

    @NotNull
    protected LocalDate startRecurrence;

    @NotNull
    protected LocalDate endRecurrence;

    @Positive
    protected int repeatEvery = 1;

    BaseEventOption(String optionType) {
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

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }
}
