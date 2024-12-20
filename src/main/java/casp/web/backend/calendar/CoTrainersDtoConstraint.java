package casp.web.backend.calendar;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CoTrainersDtoValidation.class})
public @interface CoTrainersDtoConstraint {
    String message() default
            "You cannot add a new co-trainer with the same id as in co-trainers set.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
