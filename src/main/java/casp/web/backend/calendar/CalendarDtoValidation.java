package casp.web.backend.calendar;


import casp.web.backend.calendar.presentation.BaseEventWriteRequiredFields;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class CalendarDtoValidation implements ConstraintValidator<CalendarDtoConstraint, BaseEventWriteRequiredFields> {
    @Override
    public boolean isValid(BaseEventWriteRequiredFields value, ConstraintValidatorContext context) {
        var areNewCalendarEntryAndRecurrenceOptionEmpty = ObjectUtils.allNull(value.getNewCalendarEntry(), value.getRecurrenceOption());
        var areNewCalendarEntryAndRecurrenceOptionNotEmpty = ObjectUtils.allNotNull(value.getNewCalendarEntry(), value.getRecurrenceOption());
        return !areNewCalendarEntryAndRecurrenceOptionEmpty && !areNewCalendarEntryAndRecurrenceOptionNotEmpty;
    }
}
