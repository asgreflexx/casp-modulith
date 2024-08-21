package casp.web.backend.data.access.layer.documents.event.types;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BaseEventOptionValidation.class})
public @interface BaseEventOptionConstraint {
    String message() default "Every event has only one option or none";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
