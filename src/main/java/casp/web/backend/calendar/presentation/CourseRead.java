package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.CourseRequiredFields;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CourseRead extends BaseEventRead<Space> implements CourseRequiredFields {
    private int spaceLimit;
    private Set<CoTrainer> coTrainers;

    CourseRead() {
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
}
