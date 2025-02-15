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
        void isNotPaid() {
            assertTrue(VALIDATION.isValid(payment, null));
        }

        @Test
        void isPaid() {
            when(payment.getPaidPrice()).thenReturn(1.0);
            when(payment.getPaidDate()).thenReturn(LocalDate.now());

            assertTrue(VALIDATION.isValid(payment, null));
        }
    }

    @Nested
    class IsInValid {
        @Test
        void dateIsNull() {
            when(payment.getPaidPrice()).thenReturn(1.0);

            assertFalse(VALIDATION.isValid(payment, null));
        }

        @Test
        void priceIs0() {
            when(payment.getPaidDate()).thenReturn(LocalDate.now());

            assertFalse(VALIDATION.isValid(payment, null));
        }
    }
}
