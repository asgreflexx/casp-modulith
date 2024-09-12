package casp.web.backend.data.access.layer.event.options;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventOptionTimesValidation implements ConstraintValidator<EventOptionTimesConstraint, EventOptionTimes> {

    @Override
    public boolean isValid(EventOptionTimes value, ConstraintValidatorContext context) {
        return value.getStartTime().isBefore(value.getEndTime());
    }
}
