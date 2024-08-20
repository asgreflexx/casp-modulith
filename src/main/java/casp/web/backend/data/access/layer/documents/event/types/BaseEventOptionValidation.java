package casp.web.backend.data.access.layer.documents.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BaseEventOptionValidation implements ConstraintValidator<BaseEventOptionConstraint, BaseEvent> {
    @Override
    public boolean isValid(BaseEvent value, ConstraintValidatorContext context) {
        return !(value.getDailyOption() != null && value.getWeeklyOption() != null);
    }
}
