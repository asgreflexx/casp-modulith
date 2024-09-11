package casp.web.backend.presentation.layer.dtos.event.types;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This constraint works as follows:
 * <ul>
 *     <li>If spaceLimit is negative and the participants list is null, it will return true.</li>
 *     <li>Otherwise, the quantity of participants is not higher than spaceLimit</li>
 * </ul>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CourseDtoSpaceValidation.class})
public @interface CourseDtoSpaceConstraint {
    String message() default "There are more spaces booked than spaces available";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
