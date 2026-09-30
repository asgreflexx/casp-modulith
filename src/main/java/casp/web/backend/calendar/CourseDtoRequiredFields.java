package casp.web.backend.calendar;

import java.util.Set;
import java.util.UUID;

public interface CourseDtoRequiredFields extends CourseRequiredFields {
    Set<UUID> getCoTrainerIds();

    void setCoTrainerIds(Set<UUID> coTrainerIds);
}
