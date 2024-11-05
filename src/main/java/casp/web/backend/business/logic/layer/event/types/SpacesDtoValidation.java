package casp.web.backend.business.logic.layer.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpacesDtoValidation implements ConstraintValidator<SpacesDtoConstraint, CourseDtoRequiredFields> {
    @Override
    public boolean isValid(CourseDtoRequiredFields value, ConstraintValidatorContext context) {
        return value.getNewSpaces()
                .stream()
                .noneMatch(newId -> value.getSpaces()
                        .stream()
                        .anyMatch(space -> space.getId().equals(newId)));
    }
}
