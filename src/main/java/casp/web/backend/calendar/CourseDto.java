package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CourseDto extends BaseEventDto<Space> implements CourseDtoRequiredFields {
    private int spaceLimit;
    private Set<CoTrainer> coTrainers = new HashSet<>();
    private Set<UUID> newCoTrainers = new HashSet<>();

    public CourseDto() {
        super(BaseEventType.COURSE);
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
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
