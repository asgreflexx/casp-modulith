package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.CourseRequiredFields;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CourseRead extends BaseEventRead<Space> implements CourseRequiredFields {
    private int spaceLimit;
    private Set<CoTrainer> coTrainers;

    CourseRead() {
        super(BaseEventType.COURSE);
    }

}
