package casp.web.backend.dog;

import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.dog.data.Grade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public interface DogHasHandlerRequiredFields {
    @Valid
    @NotNull
    MemberReference getMember();

    void setMember(@Valid @NotNull MemberReference member);

    @Valid
    @NotNull
    DogReference getDog();

    void setDog(@Valid @NotNull DogReference dog);

    @Valid
    @NotNull
    Set<Grade> getGrades();

    void setGrades(@Valid @NotNull Set<Grade> grades);
}
