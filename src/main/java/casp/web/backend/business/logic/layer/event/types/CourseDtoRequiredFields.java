package casp.web.backend.business.logic.layer.event.types;

import java.util.Set;
import java.util.UUID;

public interface CourseDtoRequiredFields {
    Set<UUID> getNewCoTrainers();

    void setNewCoTrainers(Set<UUID> newCoTrainers);

    Set<UUID> getNewSpaces();

    void setNewSpaces(Set<UUID> newSpaces);

    void setSpaceListSize(int spaceListSize);
}
