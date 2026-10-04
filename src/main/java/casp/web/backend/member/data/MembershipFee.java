package casp.web.backend.member.data;

import casp.web.backend.common.validation.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class MembershipFee implements Payment {
    @NotNull
    private Double paidPrice;
    @NotNull
    private LocalDate paidDate;
}
