package casp.web.backend.common.member;

import casp.web.backend.business.logic.layer.event.types.SpaceDto;
import casp.web.backend.common.enums.EntityStatus;
import jakarta.validation.Valid;

import java.util.Set;

public interface MemberDtoRequiredFields extends MemberRequiredFields {
    EntityStatus getEntityStatus();

    void setEntityStatus(EntityStatus entityStatus);

    @Valid
    Set<DogHasHandler> getDogHasHandlerSet();

    void setDogHasHandlerSet(@Valid Set<DogHasHandler> dogHasHandlerSet);

    Set<SpaceDto> getSpaces();

    void setSpaces(Set<SpaceDto> spaces);
}
