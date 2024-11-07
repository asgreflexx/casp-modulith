package casp.web.backend.common.validation;

import java.time.LocalDate;

@PaymentConstraint
public interface Payment {
    double getPaidPrice();

    LocalDate getPaidDate();

    boolean isPaid();
}
