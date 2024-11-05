package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.CourseDtoRequiredFields;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;

import java.util.Set;
import java.util.UUID;

class CourseWrite extends BaseEventWrite implements CourseDtoRequiredFields {
    private Set<CoTrainer> coTrainers;
    private Set<UUID> newCoTrainers;
    private Set<Space> spaces;
    private Set<UUID> newSpaces;
    private int spaceLimit;

    @Override
    public Set<CoTrainer> getCoTrainers() {
        return coTrainers;
    }

    @Override
    public void setCoTrainers(Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
    }

    @Override
    public Set<UUID> getNewCoTrainers() {
        return newCoTrainers;
    }

    @Override
    public void setNewCoTrainers(Set<UUID> newCoTrainers) {
        this.newCoTrainers = newCoTrainers;
    }

    @Override
    public Set<Space> getSpaces() {
        return spaces;
    }

    @Override
    public void setSpaces(Set<Space> spaces) {
        this.spaces = spaces;
    }

    @Override
    public Set<UUID> getNewSpaces() {
        return newSpaces;
    }

    @Override
    public void setNewSpaces(Set<UUID> newSpaces) {
        this.newSpaces = newSpaces;
    }

    @Override
    public int getSpaceLimit() {
        return spaceLimit;
    }

    @Override
    public void setSpaceLimit(int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
