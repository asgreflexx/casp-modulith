package at.unleashit.caspmodulith.services.members.model;

import java.time.LocalDate;

public record MembershipFee(Float price, boolean isPaid, String comment, LocalDate paidDate) {

}
