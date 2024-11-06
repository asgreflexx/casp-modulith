package casp.web.backend.business.logic.layer.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class SpacesDtoValidation implements ConstraintValidator<SpacesDtoConstraint, CourseDtoRequiredFields> {
    @Override
    public boolean isValid(CourseDtoRequiredFields value, ConstraintValidatorContext context) {
        var newSpaces = value.getNewSpaces();
        var spaces = value.getSpaces();
        if (ObjectUtils.anyNull(newSpaces, spaces)) {
            return true;
        } else {
            return newSpaces
                    .stream()
                    .noneMatch(newId -> spaces
                            .stream()
                            .anyMatch(space -> space.getId().equals(newId)));
        }
    }
}
