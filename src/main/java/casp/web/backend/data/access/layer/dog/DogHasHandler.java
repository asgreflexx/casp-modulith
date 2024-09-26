package casp.web.backend.data.access.layer.dog;

import casp.web.backend.data.access.layer.commons.BaseDocument;
import casp.web.backend.data.access.layer.member.Member;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

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
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DogHasHandler.class.getSimpleName() + "[", "]")
                .add("memberId=" + memberId)
                .add("member=" + member)
                .add("dogId=" + dogId)
                .add("dog=" + dog)
                .add("grades=" + grades)
                .add("id=" + id)
                .add("version=" + version)
                .add("createdBy='" + createdBy + "'")
                .add("created=" + created)
                .add("modifiedBy='" + modifiedBy + "'")
                .add("modified=" + modified)
                .add("entityStatus=" + entityStatus)
                .toString();
    }
}
