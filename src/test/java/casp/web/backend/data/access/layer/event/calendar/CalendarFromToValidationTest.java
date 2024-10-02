package casp.web.backend.data.access.layer.event.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CalendarFromToValidationTest {

    private static final CalendarFromToValidation VALIDATOR = new CalendarFromToValidation();
    private Calendar calendar;

    @BeforeEach
    void setUp() {
        calendar = new Calendar();
    }

    @Test
    void isValid() {
        calendar.setEventFrom(LocalDateTime.now());
        calendar.setEventTo(LocalDateTime.now().plusHours(1));

        assertTrue(VALIDATOR.isValid(calendar, null));
    }

    @Test
    void isInvalid() {
        var now = LocalDateTime.now();
        calendar.setEventFrom(now);
        calendar.setEventTo(now);

        assertFalse(VALIDATOR.isValid(calendar, null));
    }
}
