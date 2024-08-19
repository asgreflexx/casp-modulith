package casp.web.backend.data.access.layer.documents.commons;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentValidation implements ConstraintValidator<PaymentConstraint, Payment> {
    @Override
    public boolean isValid(Payment value, ConstraintValidatorContext context) {
        return value.isPaid() ?
                value.getPaidPrice() > 0 && value.getPaidDate() != null :
                value.getPaidPrice() == 0 && value.getPaidDate() == null;
    }
}
