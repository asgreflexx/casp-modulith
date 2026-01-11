package casp.web.backend.calendar.presentation;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CourseWrite extends BaseEventWrite {
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<UUID> coTrainerIds = new HashSet<>();
    private int spaceLimit;

    public Set<UUID> getCoTrainerIds() {
        return coTrainerIds;
    }

    public void setCoTrainerIds(Set<UUID> coTrainerIds) {
        this.coTrainerIds = coTrainerIds;
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
