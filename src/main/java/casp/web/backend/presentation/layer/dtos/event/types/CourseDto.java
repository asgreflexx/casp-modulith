package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.presentation.layer.dtos.event.participants.CoTrainerDto;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.HashSet;
import java.util.Set;

@CourseDtoSpaceConstraint
public class CourseDto extends BaseEventDto<SpaceDto> {
    @Valid
    @NotNull
    private Set<CoTrainerDto> coTrainers = new HashSet<>();

    @PositiveOrZero
    private int spaceLimit;

    public CourseDto() {
        super(Course.EVENT_TYPE);
    }

    public Set<CoTrainerDto> getCoTrainers() {
        return coTrainers;
    }

    public void setCoTrainers(Set<CoTrainerDto> coTrainers) {
        this.coTrainers = coTrainers;
    }

    public int getSpaceLimit() {
        return spaceLimit;
    }

    public void setSpaceLimit(final int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }
}
