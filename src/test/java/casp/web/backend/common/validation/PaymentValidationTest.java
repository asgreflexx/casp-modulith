package casp.web.backend.common.validation;

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
class PaymentValidationTest {
    private static final PaymentValidation VALIDATION = new PaymentValidation();

    @Mock
    private Payment payment;

    @Nested
    class IsValid {
        @Test
        void isPaidIsFalse() {
            when(payment.isPaid()).thenReturn(false);
            when(payment.getPaidPrice()).thenReturn(0.0);
            when(payment.getPaidDate()).thenReturn(null);

            assertTrue(VALIDATION.isValid(payment, null));
        }

        @Test
        void isPaidIsTrue() {
            when(payment.isPaid()).thenReturn(true);
            when(payment.getPaidPrice()).thenReturn(1.0);
            when(payment.getPaidDate()).thenReturn(LocalDate.now());

            assertTrue(VALIDATION.isValid(payment, null));
        }
    }

    @Nested
    class IsInValid {
        @Test
        void isPaidIsFalseAndPaidPriceIsHigherThan0() {
            when(payment.isPaid()).thenReturn(false);
            when(payment.getPaidPrice()).thenReturn(1.0);

            assertFalse(VALIDATION.isValid(payment, null));
        }

        @Test
        void isPaidIsFalseAndPaidDateIsNotNull() {
            when(payment.isPaid()).thenReturn(false);
            when(payment.getPaidPrice()).thenReturn(0.0);
            when(payment.getPaidDate()).thenReturn(LocalDate.now());

            assertFalse(VALIDATION.isValid(payment, null));
        }

        @Test
        void isPaidIsTrueAndPaidPriceIs0() {
            when(payment.isPaid()).thenReturn(true);
            when(payment.getPaidPrice()).thenReturn(0.0);

            assertFalse(VALIDATION.isValid(payment, null));
        }

        @Test
        void isPaidIsTrueAndPaidDateIsNull() {
            when(payment.isPaid()).thenReturn(true);
            when(payment.getPaidPrice()).thenReturn(1.0);
            when(payment.getPaidDate()).thenReturn(null);

            assertFalse(VALIDATION.isValid(payment, null));
        }
    }
}
