package casp.web.backend.presentation.layer.dtos.event.calendar;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarFromToValidationTest {

    private static final CalendarFromToValidation VALIDATOR = new CalendarFromToValidation();
    private CalendarDto calendar;

    @BeforeEach
    void setUp() {
        calendar = new CalendarDto();
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
