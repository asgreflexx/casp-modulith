package casp.web.backend.configuration.test;

import casp.web.backend.common.enums.EntityStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Document(collection = "member")
public class MemberProperty {
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

    @NotEmpty
    private Set<RoleTest> roles;

    public MemberProperty() {
    }

    public MemberProperty(String firstName, String lastName, String email, Set<RoleTest> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    public void setId(@NotNull UUID id) {
        this.id = id;
    }

    @NotNull
    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(@NotNull EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    @NotBlank
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank String firstName) {
        this.firstName = firstName;
    }

    @NotBlank
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank String lastName) {
        this.lastName = lastName;
    }

    @NotNull
    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull @Email String email) {
        this.email = email;
    }

    @NotEmpty
    public Set<RoleTest> getRoles() {
        return roles;
    }

    public void setRoles(@NotEmpty Set<RoleTest> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberProperty that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
