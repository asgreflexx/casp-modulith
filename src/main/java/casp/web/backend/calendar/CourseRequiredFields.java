package casp.web.backend.calendar;

import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

@CourseSpacesConstraint
public interface CourseRequiredFields {
    @PositiveOrZero
    int getSpaceLimit();

    void setSpaceLimit(@PositiveOrZero int spaceLimit);

    @NotNull
    Set<@Valid CoTrainer> getCoTrainers();

    void setCoTrainers(@NotNull Set<@Valid CoTrainer> coTrainers);

    @NotNull
    Set<@Valid Space> getSpaces();

    void setSpaces(@NotNull Set<@Valid Space> spaces);
}
