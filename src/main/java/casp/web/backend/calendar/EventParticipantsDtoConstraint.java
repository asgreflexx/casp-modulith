package casp.web.backend.calendar;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EventParticipantsDtoValidation.class})
public @interface EventParticipantsDtoConstraint {
    String message() default
            "You cannot add a new participant with the same id as in participants set.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
