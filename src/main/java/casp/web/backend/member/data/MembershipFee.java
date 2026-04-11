package casp.web.backend.member.data;

import casp.web.backend.common.validation.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class MembershipFee implements Payment {
    @Setter
    @Getter
    private String comment;
    @NotNull
    private Double paidPrice;
    @Setter
    @NotNull
    private LocalDate paidDate;

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

}
