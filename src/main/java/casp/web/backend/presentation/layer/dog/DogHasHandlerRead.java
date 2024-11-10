package casp.web.backend.presentation.layer.dog;

import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.dog.DogHasHandlerRequiredFields;
import casp.web.backend.common.dog.Grade;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DogHasHandlerRead extends BaseView implements DogHasHandlerRequiredFields {
    private MemberReference member;
    private DogReference dog;
    private Set<Grade> grades = new HashSet<>();

    @Override
    public MemberReference getMember() {
        return member;
    }

    @Override
    public void setMember(MemberReference member) {
        this.member = member;
    }

    @Override
    public DogReference getDog() {
        return dog;
    }

    @Override
    public void setDog(DogReference dog) {
        this.dog = dog;
    }

    @Override
    public Set<Grade> getGrades() {
        return grades;
    }

    @Override
    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

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
