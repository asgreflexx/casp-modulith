package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.DailyRecurrenceOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarDtoValidationTest {
    private static final CalendarDtoValidation VALIDATION = new CalendarDtoValidation();

    @Mock
    private CourseDto courseDto;

    @Test
    void newCalendarEntryAndRecurrenceOptionAreEmpty() {
        assertFalse(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void newCalendarEntryIsEmpty() {
        when(courseDto.getRecurrenceOption()).thenReturn(mock(DailyRecurrenceOption.class));

        assertTrue(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void recurrenceOptionIsEmpty() {
        when(courseDto.getNewCalendarEntry()).thenReturn(mock(CalendarEntry.class));

        assertTrue(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void newCalendarEntryAndRecurrenceOptionAreNotEmpty() {
        when(courseDto.getNewCalendarEntry()).thenReturn(mock(CalendarEntry.class));
        when(courseDto.getRecurrenceOption()).thenReturn(mock(DailyRecurrenceOption.class));

        assertFalse(VALIDATION.isValid(courseDto, null));
    }
}
