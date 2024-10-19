package casp.web.backend.business.logic.layer.event.types;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MemberReferenceDtoValidation.class})
public @interface MemberReferenceDtoConstraint {
    String message() default
            "A member is needed and the new member id cannot be the same as the actual member.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
