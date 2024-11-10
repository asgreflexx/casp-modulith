package casp.web.backend.dog;

import casp.web.backend.business.logic.layer.event.types.SpaceDto;
import jakarta.validation.Valid;

import java.util.Set;

public interface DogDtoRequiredFields extends DogRequiredFields {
    @Valid
    Set<DogHasHandler> getDogHasHandlerSet();

    void setDogHasHandlerSet(@Valid Set<DogHasHandler> dogHasHandlerSet);

    Set<SpaceDto> getSpaces();

    void setSpaces(Set<SpaceDto> spaces);
}
