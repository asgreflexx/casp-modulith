package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.data.participants.CoTrainer;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CourseWrite extends BaseEventWrite {
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<CoTrainer> coTrainers = new HashSet<>();
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<UUID> newCoTrainers = new HashSet<>();
    private int spaceLimit;

    public Set<CoTrainer> getCoTrainers() {
        return coTrainers;
    }

    public void setCoTrainers(Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
    }

    public Set<UUID> getNewCoTrainers() {
        return newCoTrainers;
    }

    public void setNewCoTrainers(Set<UUID> newCoTrainers) {
        this.newCoTrainers = newCoTrainers;
    }

    public int getSpaceLimit() {
        return spaceLimit;
    }

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
