package casp.web.backend.calendar;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {SpacesDtoValidation.class})
public @interface SpacesDtoConstraint {
    String message() default
            "You cannot add a new space with the same id as in spaces set.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
