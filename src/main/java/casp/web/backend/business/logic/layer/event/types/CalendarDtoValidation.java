package casp.web.backend.business.logic.layer.event.types;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class CalendarDtoValidation implements ConstraintValidator<CalendarDtoConstraint, CourseDto> {
    @Override
    public boolean isValid(CourseDto value, ConstraintValidatorContext context) {
        var areNewCalendarEntryAndRecurrenceOptionEmpty = ObjectUtils.allNull(value.getNewCalendarEntry(), value.getRecurrenceOption());
        var areNewCalendarEntryAndRecurrenceOptionNotEmpty = ObjectUtils.allNotNull(value.getNewCalendarEntry(), value.getRecurrenceOption());
        return !areNewCalendarEntryAndRecurrenceOptionEmpty && !areNewCalendarEntryAndRecurrenceOptionNotEmpty;
    }
}
