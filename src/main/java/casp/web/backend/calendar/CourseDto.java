package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CourseDto extends BaseEventDto<Space> implements CourseDtoRequiredFields {
    private int spaceLimit;
    private Set<CoTrainer> coTrainers = new HashSet<>();
    private Set<UUID> coTrainerIds = new HashSet<>();

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
    public Set<UUID> getCoTrainerIds() {
        return coTrainerIds;
    }

    @Override
    public void setCoTrainerIds(Set<UUID> coTrainerIds) {
        this.coTrainerIds = coTrainerIds;
    }
}
