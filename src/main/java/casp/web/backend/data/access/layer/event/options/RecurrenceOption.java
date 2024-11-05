package casp.web.backend.data.access.layer.event.options;

import casp.web.backend.common.enums.BaseRecurrenceOptionType;
import casp.web.backend.common.validation.BaseEventOptionRecurrencesConstraint;
import casp.web.backend.common.validation.BaseEventOptionValidation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@BaseEventOptionRecurrencesConstraint
public abstract class RecurrenceOption implements BaseEventOptionValidation {
    @NotNull
    protected BaseRecurrenceOptionType optionType;

    @NotNull
    protected LocalDate startRecurrence;

    @NotNull
    protected LocalDate endRecurrence;

    @Positive
    protected int repeatEvery = 1;

    protected RecurrenceOption(BaseRecurrenceOptionType optionType) {
        this.optionType = optionType;
    }

    @Override
    public LocalDate getStartRecurrence() {
        return startRecurrence;
    }

    public void setStartRecurrence(LocalDate startRecurrence) {
        this.startRecurrence = startRecurrence;
    }

    @Override
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

    public BaseRecurrenceOptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(BaseRecurrenceOptionType optionType) {
        this.optionType = optionType;
    }
}
