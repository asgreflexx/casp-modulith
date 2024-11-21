package casp.web.backend.calendar;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExamParticipantsDtoValidation implements ConstraintValidator<ExamParticipantsDtoConstraint, ExamDtoRequiredFields> {
    @Override
    public boolean isValid(ExamDtoRequiredFields value, ConstraintValidatorContext context) {
        return value.getParticipants()
                .stream()
                .noneMatch(actualParticipant ->
                        value.getNewParticipants()
                                .stream()
                                .anyMatch(newParticipantId -> newParticipantId.equals(actualParticipant.getId())));
    }
}
