package casp.web.backend.dog;

import casp.web.backend.calendar.SpaceDto;
import jakarta.validation.Valid;

import java.util.Set;

public interface DogDtoRequiredFields extends DogRequiredFields {
    @Valid
    Set<DogHasHandler> getDogHasHandlerSet();

    void setDogHasHandlerSet(@Valid Set<DogHasHandler> dogHasHandlerSet);

    Set<SpaceDto> getSpaces();

    void setSpaces(Set<SpaceDto> spaces);
}
