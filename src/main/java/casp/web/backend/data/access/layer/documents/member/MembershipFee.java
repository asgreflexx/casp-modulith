package casp.web.backend.data.access.layer.documents.member;

import casp.web.backend.data.access.layer.documents.commons.Payment;
import casp.web.backend.data.access.layer.documents.commons.PaymentConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

// Cannot be a record because of querydsl
@PaymentConstraint
public class MembershipFee implements Payment {
    private String comment;
    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    private double paidPrice;
    private boolean isPaid;
    private LocalDate paidDate;

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(final double paidPrice) {
        this.paidPrice = paidPrice;
    }

    @Override
    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(final boolean paid) {
        isPaid = paid;
    }

    @Override
    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(final LocalDate paidDate) {
        this.paidDate = paidDate;
    }
}
