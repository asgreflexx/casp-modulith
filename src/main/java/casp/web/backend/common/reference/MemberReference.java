package casp.web.backend.common.reference;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@EqualsAndHashCode(of = "id")
@Setter
@Getter
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
}
