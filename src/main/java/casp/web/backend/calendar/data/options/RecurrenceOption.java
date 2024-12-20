package casp.web.backend.calendar.data.options;

import casp.web.backend.calendar.options.BaseRecurrenceOptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public abstract LocalDateTime min();

    public abstract LocalDateTime max();
}
