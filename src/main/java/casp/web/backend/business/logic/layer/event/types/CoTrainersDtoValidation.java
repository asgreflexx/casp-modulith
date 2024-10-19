package casp.web.backend.business.logic.layer.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CoTrainersDtoValidation implements ConstraintValidator<CoTrainersDtoConstraint, CourseDto> {
    @Override
    public boolean isValid(CourseDto value, ConstraintValidatorContext context) {
        return value.getNewCoTrainers()
                .stream()
                .noneMatch(newId -> value.getCoTrainers()
                        .stream()
                        .anyMatch(coTrainer -> coTrainer.getId().equals(newId)));
    }
}
