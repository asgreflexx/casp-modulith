package casp.web.backend.presentation.layer.dtos.dog;

import casp.web.backend.data.access.layer.dog.Grade;
import casp.web.backend.presentation.layer.dtos.member.SimpleMemberDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DogHasHandlerDto {
    private UUID id = UUID.randomUUID();
    private long version;
    private LocalDateTime created;
    private LocalDateTime modified;
    @NotNull
    private UUID memberId;
    private SimpleMemberDto member;
    @NotNull
    private UUID dogId;
    private SimpleDogDto dog;
    @NotNull
    @Valid
    private Set<Grade> grades = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(final UUID memberId) {
        this.memberId = memberId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(final LocalDateTime modified) {
        this.modified = modified;
    }

    public SimpleMemberDto getMember() {
        return member;
    }

    public void setMember(final SimpleMemberDto member) {
        this.member = member;
    }

    public UUID getDogId() {
        return dogId;
    }

    public void setDogId(final UUID dogId) {
        this.dogId = dogId;
    }

    public SimpleDogDto getDog() {
        return dog;
    }

    public void setDog(final SimpleDogDto dog) {
        this.dog = dog;
    }


    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(final Set<Grade> grades) {
        this.grades = grades;
    }
}
