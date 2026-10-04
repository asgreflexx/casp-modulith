package casp.web.backend.dog.presentation;

import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.dog.DogHasHandlerRequiredFields;
import casp.web.backend.dog.data.Grade;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class DogHasHandlerRead extends BaseView implements DogHasHandlerRequiredFields {
    private MemberReference member;
    private DogReference dog;
    private Set<Grade> grades = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DogHasHandlerRead that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(member, that.member) && Objects.equals(dog, that.dog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), member, dog);
    }
}
