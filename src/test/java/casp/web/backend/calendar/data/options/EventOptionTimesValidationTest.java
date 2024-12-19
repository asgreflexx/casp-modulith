package casp.web.backend.calendar.data.options;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventOptionTimesValidationTest {
    private static final EventOptionTimesValidation VALIDATION = new EventOptionTimesValidation();

    @Mock
    private EventOptionTimes eventOptionTimes;

    @Test
    void isValid() {
        when(eventOptionTimes.getStartTime()).thenReturn(LocalTime.MIN);
        when(eventOptionTimes.getEndTime()).thenReturn(LocalTime.MAX);

        assertTrue(VALIDATION.isValid(eventOptionTimes, null));
    }

    @Test
    void isInvalid() {
        when(eventOptionTimes.getStartTime()).thenReturn(LocalTime.MIN);
        when(eventOptionTimes.getEndTime()).thenReturn(LocalTime.MIN);

        assertFalse(VALIDATION.isValid(eventOptionTimes, null));
    }
}
