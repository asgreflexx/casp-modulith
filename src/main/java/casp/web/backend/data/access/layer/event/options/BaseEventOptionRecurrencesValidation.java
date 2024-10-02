package casp.web.backend.data.access.layer.event.options;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BaseEventOptionRecurrencesValidation
        implements ConstraintValidator<BaseEventOptionRecurrencesConstraint, BaseEventOption> {
    @Override
    public boolean isValid(BaseEventOption value, ConstraintValidatorContext context) {
        return value.getStartRecurrence().isBefore(value.getEndRecurrence());
    }
}
