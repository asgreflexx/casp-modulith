package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CourseDto extends BaseEventDto<Space> implements CourseDtoRequiredFields {
    private int spaceLimit;
    private Set<CoTrainer> coTrainers = new HashSet<>();
    private Set<UUID> coTrainerIds = new HashSet<>();

    public CourseDto() {
        super(BaseEventType.COURSE);
    }
}
