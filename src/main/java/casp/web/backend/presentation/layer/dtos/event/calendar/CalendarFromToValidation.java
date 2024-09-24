package casp.web.backend.presentation.layer.dtos.event.calendar;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CalendarFromToValidation implements ConstraintValidator<CalendarFromToConstraint, CalendarDto> {
    @Override
    public boolean isValid(CalendarDto value, ConstraintValidatorContext context) {
        return value.getEventFrom().isBefore(value.getEventTo());
    }
}
