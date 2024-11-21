package casp.web.backend.calendar;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventParticipantsDtoValidation implements ConstraintValidator<EventParticipantsDtoConstraint, EventDtoRequiredFields> {
    @Override
    public boolean isValid(EventDtoRequiredFields value, ConstraintValidatorContext context) {
        return value.getNewParticipants()
                .stream()
                .noneMatch(newId -> value.getParticipants()
                        .stream()
                        .anyMatch(participant -> participant.getId().equals(newId)));
    }
}
