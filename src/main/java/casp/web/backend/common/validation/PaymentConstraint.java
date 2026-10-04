package casp.web.backend.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This constraint works as follows:
 * <ul>
 *   <li>The attribute isPaid is <b>true</b>: paidPrice is positive and paidDate isn't empty
 *   <li>The attribute isPaid is <b>false</b>: paidPrice is null or zero and paidDate is empty
 * </ul>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PaymentValidation.class})
public @interface PaymentConstraint {
    String message() default
            "If paid, then value and date must be added; if not, both value and date must be empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
