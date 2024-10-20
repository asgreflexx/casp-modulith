package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CoTrainersDtoConstraint
@SpacesDtoConstraint
public class CourseDto extends BaseEventDto implements CourseRequiredFields, CourseDtoRequiredFields {
    private int spaceLimit;
    private Set<CoTrainer> coTrainers = new HashSet<>();
    private Set<UUID> newCoTrainers = new HashSet<>();
    private Set<Space> spaces = new HashSet<>();
    private Set<UUID> newSpaces = new HashSet<>();
    private int spaceListSize;

    public CourseDto() {
        super(BaseEventType.COURSE);
    }

    @Override
    public int getSpaceLimit() {
        return spaceLimit;
    }

    @Override
    public void setSpaceLimit(final int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }

    @Override
    public Set<CoTrainer> getCoTrainers() {
        return coTrainers;
    }

    @Override
    public void setCoTrainers(final Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
    }

    @Override
    public Set<UUID> getNewCoTrainers() {
        return newCoTrainers;
    }

    @Override
    public void setNewCoTrainers(final Set<UUID> newCoTrainers) {
        this.newCoTrainers = newCoTrainers;
    }

    @Override
    public Set<Space> getSpaces() {
        return spaces;
    }

    @Override
    public void setSpaces(final Set<Space> spaces) {
        this.spaces = spaces;
    }

    @Override
    public Set<UUID> getNewSpaces() {
        return newSpaces;
    }

    @Override
    public void setNewSpaces(final Set<UUID> newSpaces) {
        this.newSpaces = newSpaces;
    }

    @Override
    public int getSpaceListSize() {
        return spaceListSize;
    }

    @Override
    public void setSpaceListSize(final int spaceListSize) {
        this.spaceListSize = spaceListSize;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
