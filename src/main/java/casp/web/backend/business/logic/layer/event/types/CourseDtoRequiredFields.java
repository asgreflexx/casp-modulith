package casp.web.backend.business.logic.layer.event.types;

import java.util.Set;
import java.util.UUID;

@CoTrainersDtoConstraint
@SpacesDtoConstraint
public interface CourseDtoRequiredFields extends BaseEventDtoRequiredFields, CourseRequiredFields {
    Set<UUID> getNewCoTrainers();

    void setNewCoTrainers(Set<UUID> newCoTrainers);

    Set<UUID> getNewSpaces();

    void setNewSpaces(Set<UUID> newSpaces);
}
