package casp.web.backend.data.access.layer.entities;

import java.time.LocalDate;

public class MembershipFee {

    private Float paidPrice;

    private boolean isPaid;

    private String comment;

    private LocalDate paidDate;

    public Float getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(Float paidPrice) {
        this.paidPrice = paidPrice;
    }

    public boolean getPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    @Override
    public String toString() {
        return "MembershipFee{" +
                ", paidPrice=" + paidPrice +
                ", isPaid=" + isPaid +
                ", comment='" + comment + '\'' +
                ", paidDate=" + paidDate +
                '}';
    }
}
