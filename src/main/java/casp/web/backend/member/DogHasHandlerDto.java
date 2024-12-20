package casp.web.backend.member;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class DogHasHandlerDto {
    @NotNull
    private UUID id;
    @NotNull
    private UUID dogId;
    @NotNull
    private String dogName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDogId() {
        return dogId;
    }

    public void setDogId(UUID dogId) {
        this.dogId = dogId;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DogHasHandlerDto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
