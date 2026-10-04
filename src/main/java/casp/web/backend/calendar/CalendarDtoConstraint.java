package casp.web.backend.calendar;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CalendarDtoValidation.class})
public @interface CalendarDtoConstraint {
    String message() default
            "Whether it has a calendar entry or a recurrence option.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
