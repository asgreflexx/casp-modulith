package casp.web.backend.data.access.layer.documents.commons;

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
            "If paid than a value and the date were added, if not booth value and date are empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
