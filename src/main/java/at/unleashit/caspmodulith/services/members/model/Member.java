package at.unleashit.caspmodulith.services.members.model;

import at.unleashit.caspmodulith.enums.EntityStatus;
import at.unleashit.caspmodulith.services.members.enums.Gender;
import at.unleashit.caspmodulith.services.members.enums.Roles;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Document
public record Member (
        @Id UUID id,
        @Version long version,
        @CreatedBy String createdBy,
        @CreatedDate LocalDateTime created,
        @LastModifiedBy String modifiedBy,
        @LastModifiedDate LocalDateTime modified,
        EntityStatus entityStatus,
        @NotBlank String firstName,
        @NotBlank String lastName,
        LocalDate birthDate,
        Gender gender,
        String telephoneNumer,
        String email,
        String address,
        String postcode,
        String city,
        Set<Roles> roles,
        Set<MembershipFee> membershipFees
        ) {
}
