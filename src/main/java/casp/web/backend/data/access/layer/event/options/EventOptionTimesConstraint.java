package casp.web.backend.data.access.layer.event.options;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This validation will check if the end time is after the start time. <br>
 * If any one of the attributes is null, it will return true, because there are the not null annotations .
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EventOptionTimesValidation.class})
public @interface EventOptionTimesConstraint {
    String message() default "The start time must be before end time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
