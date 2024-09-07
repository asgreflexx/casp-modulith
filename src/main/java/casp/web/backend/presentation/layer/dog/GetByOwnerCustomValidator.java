package casp.web.backend.presentation.layer.dog;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class GetByOwnerCustomValidator implements ConstraintValidator<GetByOwnerCustomValidation, Object[]> {
    private static final int PARAMETERS_COUNT = 3;

    @Override
    public boolean isValid(final Object[] values, final ConstraintValidatorContext context) {
        if (ArrayUtils.isEmpty(values) || values.length < PARAMETERS_COUNT) {
            return false;
        }
        var oneParameterIsNotBlank = Arrays
                .stream(values)
                .map(String::valueOf)
                .anyMatch(StringUtils::isNotBlank);
        if (!oneParameterIsNotBlank) {
            return false;
        }

        var chipNumber = convertObjectToString(values[0]);
        var name = convertObjectToString(values[1]);
        var ownerName = convertObjectToString(values[2]);

        var isChipNumberBlank = StringUtils.isBlank(chipNumber);
        var isNameAndOwnerNameAreBlank = StringUtils.isBlank(name) || StringUtils.isBlank(ownerName);

        return !isChipNumberBlank || !isNameAndOwnerNameAreBlank;
    }

    private static String convertObjectToString(final Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
