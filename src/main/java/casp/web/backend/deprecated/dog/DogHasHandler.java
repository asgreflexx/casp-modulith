package casp.web.backend.deprecated.dog;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.deprecated.member.Member;
import casp.web.backend.dog.data.Dog;
import casp.web.backend.dog.data.Grade;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated use {@link casp.web.backend.dog.data.DogHasHandler} instead. It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@QueryEntity
@Document
public class DogHasHandler extends BaseDocument {

    @NotNull
    private UUID memberId;
    @Valid
    @DBRef
    private Member member;

    @NotNull
    private UUID dogId;
    @Valid
    @DBRef
    private Dog dog;

    @NotNull
    @Valid
    private Set<Grade> grades = new HashSet<>();

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public UUID getDogId() {
        return dogId;
    }

    public void setDogId(UUID dogId) {
        this.dogId = dogId;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
