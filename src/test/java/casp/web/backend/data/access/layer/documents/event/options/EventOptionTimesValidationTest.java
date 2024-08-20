package casp.web.backend.data.access.layer.documents.event.options;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventOptionTimesValidationTest {

    public static final EventOptionTimesValidation VALIDATION = new EventOptionTimesValidation();
    private DailyEventOption dailyEventOption;

    @BeforeEach
    void setUp() {
        dailyEventOption = new DailyEventOption();
    }

    @Test
    void isValid() {
        dailyEventOption.setStartTime(LocalTime.MIN);
        dailyEventOption.setEndTime(LocalTime.MAX);

        assertTrue(VALIDATION.isValid(dailyEventOption, null));
    }

    @Test
    void isInvalid() {
        dailyEventOption.setStartTime(LocalTime.MIN);
        dailyEventOption.setEndTime(LocalTime.MIN);

        assertFalse(VALIDATION.isValid(dailyEventOption, null));
    }
}
