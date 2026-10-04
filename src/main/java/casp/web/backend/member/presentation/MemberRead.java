package casp.web.backend.member.presentation;

import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.enums.Gender;
import casp.web.backend.member.MemberDtoRequiredFields;
import casp.web.backend.member.data.Card;
import casp.web.backend.member.data.MembershipFee;
import casp.web.backend.member.data.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
class MemberRead extends BaseView implements MemberDtoRequiredFields {
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
    private Set<Role> roles;
    private Set<MembershipFee> membershipFees;
    private Set<Card> cards;
}
