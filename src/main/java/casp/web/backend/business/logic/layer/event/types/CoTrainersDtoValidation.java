package casp.web.backend.business.logic.layer.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CoTrainersDtoValidation implements ConstraintValidator<CoTrainersDtoConstraint, CourseDtoRequiredFields> {
    @Override
    public boolean isValid(CourseDtoRequiredFields value, ConstraintValidatorContext context) {
        return value.getNewCoTrainers()
                .stream()
                .noneMatch(newId -> value.getCoTrainers()
                        .stream()
                        .anyMatch(coTrainer -> coTrainer.getId().equals(newId)));
    }
}
