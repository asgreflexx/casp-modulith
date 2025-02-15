package casp.web.backend.deprecated.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class BaseEventOptionValidation implements ConstraintValidator<BaseEventOptionConstraint, BaseEvent> {
    @Override
    public boolean isValid(BaseEvent value, ConstraintValidatorContext context) {
        return !(value.getDailyOption() != null && value.getWeeklyOption() != null);
    }
}
