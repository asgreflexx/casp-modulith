package casp.web.backend.calendar;

import java.util.Set;
import java.util.UUID;

@CoTrainersDtoConstraint
@SpacesDtoConstraint
public interface CourseDtoRequiredFields extends CourseRequiredFields {
    Set<UUID> getNewCoTrainers();

    void setNewCoTrainers(Set<UUID> newCoTrainers);

    Set<UUID> getNewSpaces();

    void setNewSpaces(Set<UUID> newSpaces);
}
