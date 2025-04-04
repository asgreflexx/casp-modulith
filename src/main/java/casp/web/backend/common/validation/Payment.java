package casp.web.backend.common.validation;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@PaymentConstraint
public interface Payment {
    @Positive
    @Digits(integer = 9, fraction = 2)
    Double getPaidPrice();

    void setPaidPrice(@Positive
                      @Digits(integer = 9, fraction = 2)
                      Double paidPrice);

    LocalDate getPaidDate();

    void setPaidDate(LocalDate paidDate);
}
