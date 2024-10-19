package casp.web.backend.common.reference;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.UUID;

@QueryEntity
@Document(collection = "member")
public class MemberReference {
    @Id
    @NotNull
    private UUID id = UUID.randomUUID();

    @NotNull
    private EntityStatus entityStatus = EntityStatus.ACTIVE;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @Email
    private String email;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(final EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberReference that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
