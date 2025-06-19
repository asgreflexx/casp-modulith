package casp.web.backend.calendar.data.options;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RecurrenceValidation
        implements ConstraintValidator<RecurrenceConstraint, BaseEventOptionValidation> {
    private int plusDays;

    @Override
    public void initialize(final RecurrenceConstraint constraintAnnotation) {
        plusDays = constraintAnnotation.plusDays();
    }

    @Override
    public boolean isValid(BaseEventOptionValidation value, ConstraintValidatorContext context) {
        return !value.getStartRecurrence().plusDays(plusDays).isAfter(value.getEndRecurrence());
    }
}
