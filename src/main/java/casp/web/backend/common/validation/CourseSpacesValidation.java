package casp.web.backend.common.validation;

import casp.web.backend.business.logic.layer.event.types.CourseRequiredFields;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CourseSpacesValidation implements ConstraintValidator<CourseSpacesConstraint, CourseRequiredFields> {
    @Override
    public boolean isValid(CourseRequiredFields value, ConstraintValidatorContext context) {
        return value.getSpaceListSize() <= value.getSpaceLimit();
    }
}
