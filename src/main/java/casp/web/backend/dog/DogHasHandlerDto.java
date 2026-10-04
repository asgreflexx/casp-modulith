package casp.web.backend.dog;

import casp.web.backend.common.base.BaseDto;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.dog.data.Grade;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class DogHasHandlerDto extends BaseDto implements DogHasHandlerRequiredFields, DogHasHandlerDtoRequiredFields {
    private MemberReference member;
    private DogReference dog;
    private Set<Grade> grades = new HashSet<>();
    private UUID memberId;
    private UUID dogId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DogHasHandlerDto that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(member, that.member) && Objects.equals(dog, that.dog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), member, dog);
    }
}
