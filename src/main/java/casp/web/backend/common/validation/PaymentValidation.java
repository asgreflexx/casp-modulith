package casp.web.backend.common.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentValidation implements ConstraintValidator<PaymentConstraint, Payment> {
    @Override
    public boolean isValid(Payment payment, ConstraintValidatorContext context) {
        return isPaid(payment) || isNotPaid(payment);
    }

    private static boolean isPaid(Payment payment) {
        return payment.getPaidDate() != null && payment.getPaidPrice() != null;
    }

    private static boolean isNotPaid(Payment payment) {
        return payment.getPaidDate() == null && payment.getPaidPrice() == null;
    }
}
