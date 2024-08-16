package casp.web.backend.presentation.layer;

import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.entities.Grade;
import casp.web.backend.data.access.layer.entities.Member;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class DogHasHandlerDto {

    private UUID id;

    private Member member;

    private Dog dog;

    private Set<Grade> grades = new HashSet<>();

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }
}
