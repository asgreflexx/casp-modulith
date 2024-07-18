package at.unleashit.caspmodulith.services.calendar.model.events;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CalendarFromToValidation
        implements ConstraintValidator<CalendarFromToConstraint, Calendar> {
    @Override
    public boolean isValid(Calendar value, ConstraintValidatorContext context) {
        if (value.getEventFrom() != null && value.getEventTo() != null) {
            return value.getEventFrom().isBefore(value.getEventTo());
        } else {
            return true;
        }
    }
}
