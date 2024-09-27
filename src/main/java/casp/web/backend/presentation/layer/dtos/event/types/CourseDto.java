package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.presentation.layer.dtos.event.participants.CoTrainerDto;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CourseDtoSpaceConstraint
public class CourseDto extends BaseEventDto<SpaceDto> {
    @Valid
    private Set<CoTrainerDto> coTrainersToRead = new HashSet<>();
    @NotNull
    private Set<UUID> coTrainersIdToRead = new HashSet<>();

    @PositiveOrZero
    private int spaceLimit;

    public CourseDto() {
        super(Course.EVENT_TYPE);
    }

    public Set<CoTrainerDto> getCoTrainersToRead() {
        return coTrainersToRead;
    }

    public void setCoTrainersToRead(Set<CoTrainerDto> coTrainersToRead) {
        this.coTrainersToRead = coTrainersToRead;
    }

    public Set<UUID> getCoTrainersIdToRead() {
        return coTrainersIdToRead;
    }

    public void setCoTrainersIdToRead(final Set<UUID> coTrainersIdToRead) {
        this.coTrainersIdToRead = coTrainersIdToRead;
    }

    public int getSpaceLimit() {
        return spaceLimit;
    }

    public void setSpaceLimit(final int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }
}
