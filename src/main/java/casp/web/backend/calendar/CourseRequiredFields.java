package casp.web.backend.calendar;

import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.calendar.presentation.BaseEventWriteRequiredFields;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

@CourseSpacesConstraint
public interface CourseRequiredFields extends BaseEventWriteRequiredFields {
    @PositiveOrZero
    int getSpaceLimit();

    void setSpaceLimit(@PositiveOrZero int spaceLimit);

    @Valid
    @NotNull
    Set<CoTrainer> getCoTrainers();

    void setCoTrainers(@Valid @NotNull Set<CoTrainer> coTrainers);

    @Valid
    @NotNull
    Set<Space> getSpaces();

    void setSpaces(@Valid @NotNull Set<Space> spaces);
}
