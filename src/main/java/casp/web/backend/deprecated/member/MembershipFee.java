package casp.web.backend.deprecated.member;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class MembershipFee {
    private String comment;
    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    private double paidPrice;
    private boolean isPaid;
    private LocalDate paidDate;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(double paidPrice) {
        this.paidPrice = paidPrice;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }
}
