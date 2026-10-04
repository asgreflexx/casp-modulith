package casp.web.backend.dog.data;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.dog.DogHasHandlerRequiredFields;
import com.querydsl.core.annotations.QueryEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
// The values are derived from the DBRef fields, so the ignore parameters are unnecessary.
@SuppressWarnings("java:S1172")
@QueryEntity
@Document
public class DogHasHandler extends BaseDocument implements DogHasHandlerRequiredFields {
    @DBRef
    private MemberReference member;
    private String firstName;
    private String lastName;
    @DBRef
    private DogReference dog;
    private String dogName;
    private String ownerName;
    private Set<Grade> grades = new HashSet<>();


    public String getFirstName() {
        this.firstName = member.getFirstName();
        return firstName;
    }

    public void setFirstName(String ignore) {
        this.firstName = this.member.getFirstName();
    }

    public String getLastName() {
        this.lastName = member.getLastName();
        return lastName;
    }

    public void setLastName(String ignore) {
        this.lastName = member.getLastName();
    }

    public String getDogName() {
        this.dogName = dog.getName();
        return dogName;
    }

    public void setDogName(String ignore) {
        this.dogName = dog.getName();
    }

    public String getOwnerName() {
        this.ownerName = dog.getOwnerName();
        return ownerName;
    }

    public void setOwnerName(String ignore) {
        this.ownerName = dog.getOwnerName();
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
