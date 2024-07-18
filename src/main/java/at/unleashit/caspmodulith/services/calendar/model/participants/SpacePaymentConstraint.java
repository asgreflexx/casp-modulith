package at.unleashit.caspmodulith.services.calendar.model.participants;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {SpacePaymentValidation.class})
public @interface SpacePaymentConstraint {
    String message() default
            "If paid than a value and the date were added, if not booth value and date are empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
