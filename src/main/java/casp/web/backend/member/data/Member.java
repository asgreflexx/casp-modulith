package casp.web.backend.member.data;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.Gender;
import casp.web.backend.member.MemberRequiredFields;
import com.querydsl.core.annotations.QueryEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@QueryEntity
@Document
public class Member extends BaseDocument implements MemberRequiredFields {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String telephoneNumber;
    // cf. not casp.web.backend.common.enums.EntityStatus.DELETED
    // MongoDB is rejecting the index because $ne is not supported in partial indexes.
    @Indexed(
            unique = true,
            partialFilter = "{ 'entityStatus': { '$in': ['ACTIVE', 'INACTIVE'] } }"
    )
    private String email;
    private String address;
    private String postcode;
    private String city;
    private Set<Role> roles = new HashSet<>(List.of(Role.USER));
    private Set<MembershipFee> membershipFees = new HashSet<>();
    private Set<Card> cards = new HashSet<>();
}
