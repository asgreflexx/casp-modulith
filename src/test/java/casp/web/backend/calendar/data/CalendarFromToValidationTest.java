package casp.web.backend.calendar.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarFromToValidationTest {
    private static final CalendarFromToValidation VALIDATION = new CalendarFromToValidation();

    @Mock
    private CalendarValidation calendarValidation;

    @Test
    void isValid() {
        when(calendarValidation.getEntryFromODT()).thenReturn(OffsetDateTime.MIN);
        when(calendarValidation.getEntryToODT()).thenReturn(OffsetDateTime.MAX);

        assertTrue(VALIDATION.isValid(calendarValidation, null));
    }

    @Test
    void isInvalid() {
        when(calendarValidation.getEntryFromODT()).thenReturn(OffsetDateTime.MIN);
        when(calendarValidation.getEntryToODT()).thenReturn(OffsetDateTime.MIN);

        assertFalse(VALIDATION.isValid(calendarValidation, null));
    }
}
