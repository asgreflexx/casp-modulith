package casp.web.backend.common.member;

import casp.web.backend.common.validation.Payment;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDate;

public class MembershipFee implements Payment {
    private String comment;
    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    private double paidPrice;
    private LocalDate paidDate;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(double paidPrice) {
        this.paidPrice = paidPrice;
    }

    @Override
    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    @Override
    public boolean isPaid() {
        return ObjectUtils.allNotNull(paidDate, paidPrice);
    }
}
