package at.unleashit.caspmodulith.services.calendar.model.participants;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpacePaymentValidation implements ConstraintValidator<SpacePaymentConstraint, Space> {
    @Override
    public boolean isValid(Space value, ConstraintValidatorContext context) {
        return value.getIsPaid() ?
                value.getPaidPrice() > 0 && value.getPaidDate() != null :
                value.getPaidPrice() == 0 && value.getPaidDate() == null;
    }
}
