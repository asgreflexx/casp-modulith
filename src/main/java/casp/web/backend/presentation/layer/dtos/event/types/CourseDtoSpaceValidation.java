package casp.web.backend.presentation.layer.dtos.event.types;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

public class CourseDtoSpaceValidation implements ConstraintValidator<CourseDtoSpaceConstraint, CourseDto> {
    @Override
    public boolean isValid(CourseDto value, ConstraintValidatorContext context) {
        return CollectionUtils.isEmpty(value.getParticipants()) || value.getParticipants().size() <= value.getSpaceLimit();
    }
}
