package casp.web.backend.calendar.data.options;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecurrenceValidationTest {
    private static final RecurrenceValidation VALIDATION = new RecurrenceValidation();
    @Mock
    private RecurrenceConstraint recurrenceConstraint;

    @Nested
    class Daily {
        private DailyRecurrenceOption dailyRecurrenceOption;

        @BeforeEach
        void setUp() {
            when(recurrenceConstraint.plusDays()).thenReturn(1);
            VALIDATION.initialize(recurrenceConstraint);
            dailyRecurrenceOption = new DailyRecurrenceOption();
        }

        @Test
        void isValid() {
            dailyRecurrenceOption.setStartRecurrence(LocalDate.now());
            dailyRecurrenceOption.setEndRecurrence(LocalDate.now().plusDays(1));

            assertTrue(VALIDATION.isValid(dailyRecurrenceOption, null));
        }

        @Test
        void isInvalid() {
            dailyRecurrenceOption.setStartRecurrence(LocalDate.now());
            dailyRecurrenceOption.setEndRecurrence(LocalDate.now());

            assertFalse(VALIDATION.isValid(dailyRecurrenceOption, null));
        }
    }

    @Nested
    class Weekly {
        private WeeklyRecurrenceOption weeklyRecurrenceOption;

        @BeforeEach
        void setUp() {
            when(recurrenceConstraint.plusDays()).thenReturn(6);
            VALIDATION.initialize(recurrenceConstraint);
            weeklyRecurrenceOption = new WeeklyRecurrenceOption();
        }

        @Test
        void isValid() {
            weeklyRecurrenceOption.setStartRecurrence(LocalDate.now());
            weeklyRecurrenceOption.setEndRecurrence(LocalDate.now().plusDays(6));

            assertTrue(VALIDATION.isValid(weeklyRecurrenceOption, null));
        }

        @Test
        void isInvalid() {
            weeklyRecurrenceOption.setStartRecurrence(LocalDate.now());
            weeklyRecurrenceOption.setEndRecurrence(LocalDate.now().plusDays(1));

            assertFalse(VALIDATION.isValid(weeklyRecurrenceOption, null));
        }
    }
}
