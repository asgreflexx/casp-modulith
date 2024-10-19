package casp.web.backend.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecurrenceOptionRecurrencesValidationTest {
    private static final BaseEventOptionRecurrencesValidation VALIDATION = new BaseEventOptionRecurrencesValidation();

    @Mock
    private BaseEventOptionValidation baseEventOptionValidation;

    @Test
    void isValid() {
        when(baseEventOptionValidation.getStartRecurrence()).thenReturn(LocalDate.MIN);
        when(baseEventOptionValidation.getEndRecurrence()).thenReturn(LocalDate.MAX);

        assertTrue(VALIDATION.isValid(baseEventOptionValidation, null));
    }

    @Test
    void isInvalid() {
        when(baseEventOptionValidation.getStartRecurrence()).thenReturn(LocalDate.MIN);
        when(baseEventOptionValidation.getEndRecurrence()).thenReturn(LocalDate.MIN);

        assertFalse(VALIDATION.isValid(baseEventOptionValidation, null));
    }
}
