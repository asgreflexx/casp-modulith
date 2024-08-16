package casp.web.backend.data.access.layer.entities;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document
public class DogHasHandler extends BaseEntity {

    @NotNull
    private UUID memberId;

    @NotNull
    private UUID dogId;

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

    public UUID getDogId() {
        return dogId;
    }

    public void setDogId(UUID dogId) {
        this.dogId = dogId;
    }

    @Override
    public String toString() {
        return "DogHasHandler{" +
                "memberId=" + memberId +
                ", dogId=" + dogId +
                ", grades=" + grades +
                '}';
    }
}
