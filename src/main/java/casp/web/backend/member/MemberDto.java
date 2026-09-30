package casp.web.backend.member;

import casp.web.backend.common.base.BaseDto;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.enums.Gender;
import casp.web.backend.member.data.Card;
import casp.web.backend.member.data.MembershipFee;
import casp.web.backend.member.data.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class MemberDto extends BaseDto implements MemberDtoRequiredFields {
    private EntityStatus entityStatus;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String telephoneNumber;
    private String email;
    private String address;
    private String postcode;
    private String city;
    private Set<Role> roles = new HashSet<>();
    private Set<MembershipFee> membershipFees = new HashSet<>();
    private Set<Card> cards = new HashSet<>();
}
