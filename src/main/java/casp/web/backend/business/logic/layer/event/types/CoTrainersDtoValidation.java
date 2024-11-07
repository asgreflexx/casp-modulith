package casp.web.backend.business.logic.layer.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CoTrainersDtoValidation implements ConstraintValidator<CoTrainersDtoConstraint, CourseDtoRequiredFields> {
    @Override
    public boolean isValid(CourseDtoRequiredFields value, ConstraintValidatorContext context) {
        var newCoTrainers = value.getNewCoTrainers();
        var coTrainerSet = value.getCoTrainers();
        return newCoTrainers
                .stream()
                .noneMatch(newId -> coTrainerSet
                        .stream()
                        .anyMatch(coTrainer -> coTrainer.getId().equals(newId)));
    }
}
