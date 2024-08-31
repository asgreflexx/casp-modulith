package casp.web.backend.business.logic.layer.events.dtos;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

public class CourseDtoSpaceValidation implements ConstraintValidator<CourseDtoSpaceConstraint, CourseDto> {
    @Override
    public boolean isValid(CourseDto value, ConstraintValidatorContext context) {
        return CollectionUtils.isEmpty(value.getParticipants()) || value.getParticipants().size() <= value.getSpaceLimit();
    }
}
