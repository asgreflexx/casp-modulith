package casp.web.backend.member;

import casp.web.backend.business.logic.layer.event.types.SpaceDto;
import casp.web.backend.common.enums.EntityStatus;
import jakarta.validation.Valid;

import java.util.Set;

public interface MemberDtoRequiredFields extends MemberRequiredFields {
    EntityStatus getEntityStatus();

    void setEntityStatus(EntityStatus entityStatus);

    @Valid
    Set<DogHasHandlerDto> getDogHasHandlerSet();

    void setDogHasHandlerSet(@Valid Set<DogHasHandlerDto> dogHasHandlerSet);

    Set<SpaceDto> getSpaces();

    void setSpaces(Set<SpaceDto> spaces);
}
