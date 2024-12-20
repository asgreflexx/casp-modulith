package casp.web.backend.dog;

import casp.web.backend.common.base.BaseDto;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.dog.data.Grade;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class DogHasHandlerDto extends BaseDto implements DogHasHandlerRequiredFields, DogHasHandlerDtoRequiredFields {
    private MemberReference member;
    private DogReference dog;
    private Set<Grade> grades = new HashSet<>();
    private UUID memberId;
    private UUID dogId;

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
    public UUID getMemberId() {
        return memberId;
    }

    @Override
    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    @Override
    public UUID getDogId() {
        return dogId;
    }

    @Override
    public void setDogId(UUID dogId) {
        this.dogId = dogId;
    }

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
