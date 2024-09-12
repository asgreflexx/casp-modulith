package casp.web.backend.data.access.layer.event.calendar;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CalendarFromToValidation implements ConstraintValidator<CalendarFromToConstraint, Calendar> {
    @Override
    public boolean isValid(Calendar value, ConstraintValidatorContext context) {
        return value.getEventFrom().isBefore(value.getEventTo());
    }
}
