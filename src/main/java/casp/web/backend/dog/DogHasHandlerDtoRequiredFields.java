package casp.web.backend.dog;

import casp.web.backend.dog.data.Grade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public interface DogHasHandlerDtoRequiredFields {
    Set<@Valid Grade> getGrades();

    void setGrades(Set<@Valid Grade> grades);

    @NotNull
    UUID getMemberId();

    void setMemberId(@NotNull UUID memberId);

    @NotNull
    UUID getDogId();

    void setDogId(@NotNull UUID dogId);
}
