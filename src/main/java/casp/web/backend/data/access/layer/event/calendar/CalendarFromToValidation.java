package casp.web.backend.data.access.layer.event.calendar;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CalendarFromToValidation implements ConstraintValidator<CalendarFromToConstraint, CalendarValidation> {
    @Override
    public boolean isValid(CalendarValidation value, ConstraintValidatorContext context) {
        return value.getEventFrom().isBefore(value.getEventTo());
    }
}
