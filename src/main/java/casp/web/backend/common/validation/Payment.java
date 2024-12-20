package casp.web.backend.common.validation;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@PaymentConstraint
public interface Payment {
    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    double getPaidPrice();

    LocalDate getPaidDate();
}
