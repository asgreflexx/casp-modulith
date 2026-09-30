package casp.web.backend.calendar.data;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CalendarFromToValidation implements ConstraintValidator<CalendarFromToConstraint, CalendarValidation> {
    @Override
    public boolean isValid(CalendarValidation value, ConstraintValidatorContext context) {
        return value.getEntryFromODT().isBefore(value.getEntryToODT());
    }
}
