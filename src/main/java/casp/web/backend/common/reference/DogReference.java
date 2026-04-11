package casp.web.backend.common.reference;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.enums.Gender;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(of = "id")
@Setter
@Getter
@QueryEntity
@Document(collection = "dog")
public class DogReference {
    @Id
    @NotNull
    private UUID id = UUID.randomUUID();
    @NotNull
    private EntityStatus entityStatus = EntityStatus.ACTIVE;
    @NotBlank
    private String name;
    @NotBlank
    private String ownerName;
    private String breederName;
    private String breedName;
    private LocalDate birthDate;
    private Gender gender = Gender.FEMALE;
}
