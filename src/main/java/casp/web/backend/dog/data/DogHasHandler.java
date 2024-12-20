package casp.web.backend.dog.data;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.dog.DogHasHandlerRequiredFields;
import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@QueryEntity
@Document
public class DogHasHandler extends BaseDocument implements DogHasHandlerRequiredFields {

    @DBRef
    private MemberReference member;

    @DBRef
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
        if (!(o instanceof DogHasHandler that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(member, that.member) && Objects.equals(dog, that.dog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member.getId(), dog.getId());
    }
}
