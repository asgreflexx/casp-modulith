package casp.web.backend.data.access.layer.member;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.Gender;
import casp.web.backend.common.enums.Role;
import casp.web.backend.common.member.Card;
import casp.web.backend.common.member.MemberRequiredFields;
import casp.web.backend.common.member.MembershipFee;
import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@QueryEntity
@Document
public class Member extends BaseDocument implements MemberRequiredFields {
    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private Gender gender;

    private String telephoneNumber;

    @Indexed(unique = true)
    private String email;

    private String address;

    private String postcode;

    private String city;

    private Set<Role> roles = new HashSet<>(List.of(Role.USER));

    private Set<MembershipFee> membershipFees = new HashSet<>();

    private Set<Card> cards = new HashSet<>();

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    @Override
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Set<MembershipFee> getMembershipFees() {
        return membershipFees;
    }

    @Override
    public void setMembershipFees(Set<MembershipFee> membershipFees) {
        this.membershipFees = membershipFees;
    }

    @Override
    public Set<Card> getCards() {
        return cards;
    }

    @Override
    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
