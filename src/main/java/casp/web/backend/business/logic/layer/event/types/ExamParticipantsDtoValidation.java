package casp.web.backend.business.logic.layer.event.types;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExamParticipantsDtoValidation implements ConstraintValidator<ExamParticipantsDtoConstraint, ExamDto> {
    @Override
    public boolean isValid(final ExamDto value, final ConstraintValidatorContext context) {
        return value.getParticipants()
                .stream()
                .noneMatch(actualParticipant ->
                        value.getNewParticipants()
                                .stream()
                                .anyMatch(newParticipantId -> newParticipantId.equals(actualParticipant.getId())));
    }
}
