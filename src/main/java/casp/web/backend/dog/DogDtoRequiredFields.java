package casp.web.backend.dog;

import casp.web.backend.calendar.SpaceDto;

import java.util.Set;

public interface DogDtoRequiredFields extends DogRequiredFields {
    Set<SpaceDto> getSpaces();

    void setSpaces(Set<SpaceDto> spaces);
}
