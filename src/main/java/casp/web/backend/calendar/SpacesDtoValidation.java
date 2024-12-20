package casp.web.backend.calendar;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpacesDtoValidation implements ConstraintValidator<SpacesDtoConstraint, CourseDtoRequiredFields> {
    @Override
    public boolean isValid(CourseDtoRequiredFields value, ConstraintValidatorContext context) {
        var newSpaces = value.getNewSpaces();
        var spaces = value.getSpaces();
        return newSpaces
                .stream()
                .noneMatch(newId -> spaces
                        .stream()
                        .anyMatch(space -> space.getId().equals(newId)));
    }
}
