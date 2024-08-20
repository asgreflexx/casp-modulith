package casp.web.backend.data.access.layer.documents.event.options;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseEventOptionRecurrencesValidationTest {

    public static final BaseEventOptionRecurrencesValidation VALIDATION = new BaseEventOptionRecurrencesValidation();
    private DailyEventOption dailyEventOption;

    @BeforeEach
    void setUp() {
        dailyEventOption = new DailyEventOption();
    }

    @Test
    void isValid() {
        dailyEventOption.setStartRecurrence(LocalDate.now());
        dailyEventOption.setEndRecurrence(LocalDate.now().plusDays(1));

        assertTrue(VALIDATION.isValid(dailyEventOption, null));
    }

    @Test
    void isInvalid() {
        var now = LocalDate.now();
        dailyEventOption.setStartRecurrence(now);
        dailyEventOption.setEndRecurrence(now);

        assertFalse(VALIDATION.isValid(dailyEventOption, null));
    }
}
