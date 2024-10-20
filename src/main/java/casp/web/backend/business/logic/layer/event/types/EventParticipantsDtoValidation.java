package casp.web.backend.business.logic.layer.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventParticipantsDtoValidation implements ConstraintValidator<EventParticipantsDtoConstraint, EventDto> {
    @Override
    public boolean isValid(EventDto value, ConstraintValidatorContext context) {
        return value.getNewParticipants()
                .stream()
                .noneMatch(newId -> value.getParticipants()
                        .stream()
                        .anyMatch(participant -> participant.getId().equals(newId)));
    }
}
