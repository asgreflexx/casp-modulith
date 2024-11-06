package casp.web.backend.business.logic.layer.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class CoTrainersDtoValidation implements ConstraintValidator<CoTrainersDtoConstraint, CourseDtoRequiredFields> {
    @Override
    public boolean isValid(CourseDtoRequiredFields value, ConstraintValidatorContext context) {
        var newCoTrainers = value.getNewCoTrainers();
        var coTrainerSet = value.getCoTrainers();
        if (ObjectUtils.anyNull(newCoTrainers, coTrainerSet)) {
            return true;
        } else {
            return newCoTrainers
                    .stream()
                    .noneMatch(newId -> coTrainerSet
                            .stream()
                            .anyMatch(coTrainer -> coTrainer.getId().equals(newId)));
        }
    }
}
