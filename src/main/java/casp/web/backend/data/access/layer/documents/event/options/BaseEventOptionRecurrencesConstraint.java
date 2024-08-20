package casp.web.backend.data.access.layer.documents.event.options;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This validation will check if the end recurrence is after the start recurrence. <br>
 * If any one of the attributes is null, it will return true, because there are the not null annotations .
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BaseEventOptionRecurrencesValidation.class})
public @interface BaseEventOptionRecurrencesConstraint {
    String message() default "The start recurrence date must be before end recurrence date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
