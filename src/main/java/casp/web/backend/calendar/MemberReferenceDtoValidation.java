package casp.web.backend.calendar;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class MemberReferenceDtoValidation implements ConstraintValidator<MemberReferenceDtoConstraint, BaseEventDtoWriteRequiredFields> {
    @Override
    public boolean isValid(BaseEventDtoWriteRequiredFields value, ConstraintValidatorContext context) {
        if (ObjectUtils.allNull(value.getNewMemberId(), value.getMember())) {
            return false;
        } else if (ObjectUtils.allNotNull(value.getNewMemberId(), value.getMember())) {
            return !value.getNewMemberId().equals(value.getMember().getId());
        } else {
            return true;
        }
    }
}
