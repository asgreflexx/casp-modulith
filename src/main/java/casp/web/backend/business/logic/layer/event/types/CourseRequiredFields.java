package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.validation.CourseSpacesConstraint;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

@CourseSpacesConstraint
public interface CourseRequiredFields extends BaseEventRequiredFields {
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

    int getSpaceListSize();
}
