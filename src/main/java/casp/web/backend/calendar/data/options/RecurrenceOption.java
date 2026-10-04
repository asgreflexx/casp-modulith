package casp.web.backend.calendar.data.options;

import casp.web.backend.calendar.data.BaseRecurrenceOptionType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "optionType")
@JsonSubTypes({@JsonSubTypes.Type(value = DailyRecurrenceOption.class, name = "DAILY"), @JsonSubTypes.Type(value = WeeklyRecurrenceOption.class, name = "WEEKLY")})
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
}
