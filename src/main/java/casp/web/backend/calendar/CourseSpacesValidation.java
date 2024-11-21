package casp.web.backend.calendar;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CourseSpacesValidation implements ConstraintValidator<CourseSpacesConstraint, CourseRequiredFields> {
    @Override
    public boolean isValid(CourseRequiredFields value, ConstraintValidatorContext context) {
        return value.getSpaces().size() <= value.getSpaceLimit();
    }
}
