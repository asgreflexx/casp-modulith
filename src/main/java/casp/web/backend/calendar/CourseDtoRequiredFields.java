package casp.web.backend.calendar;

import java.util.Set;
import java.util.UUID;

public interface CourseDtoRequiredFields extends CourseRequiredFields {
    Set<UUID> getNewCoTrainers();

    void setNewCoTrainers(Set<UUID> newCoTrainers);
}
