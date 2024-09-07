package casp.web.backend.presentation.layer.dog;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GetByOwnerCustomValidator.class)
@Documented
public @interface GetByOwnerCustomValidation {
    String message() default "Either the chipNumber must be provided, or the dog name and the dog ownerName must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String chipNumberParameter();

    String nameParameter();

    String ownerNameParameter();
}
