package casp.web.backend.member.data;

import casp.web.backend.common.validation.Payment;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MembershipFee implements Payment {
    private String comment;
    @NotNull
    private Double paidPrice;
    @NotNull
    private LocalDate paidDate;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public Double getPaidPrice() {
        return paidPrice;
    }

    @Override
    public void setPaidPrice(Double paidPrice) {
        this.paidPrice = paidPrice;
    }

    @Override
    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }
}
