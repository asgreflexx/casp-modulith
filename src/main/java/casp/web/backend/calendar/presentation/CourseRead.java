package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.CourseRequiredFields;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;

import java.util.Set;

public class CourseRead extends BaseEventRead implements CourseRequiredFields {
    private int spaceLimit;
    private Set<CoTrainer> coTrainers;
    private Set<Space> spaces;

    @Override
    public int getSpaceLimit() {
        return spaceLimit;
    }

    @Override
    public void setSpaceLimit(int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }

    @Override
    public Set<CoTrainer> getCoTrainers() {
        return coTrainers;
    }

    @Override
    public void setCoTrainers(Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
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
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
