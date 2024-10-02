package casp.web.backend.data.access.layer.commons;

import casp.web.backend.data.access.layer.event.participants.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentValidationTest {

    public static final PaymentValidation VALIDATION = new PaymentValidation();
    private Space space;

    @BeforeEach
    void setUp() {
        space = new Space();
    }

    @Nested
    class IsValid {
        @Test
        void paidIsFalse() {
            assertTrue(VALIDATION.isValid(space, null));
        }

        @Test
        void paidIsTrue() {
            space.setIsPaid(true);
            space.setPaidDate(LocalDate.now());
            space.setPaidPrice(1);
            assertTrue(VALIDATION.isValid(space, null));
        }
    }

    @Nested
    class IsInvalid {
        @Test
        void paidIsFalse() {
            space.setPaidDate(LocalDate.now());
            space.setPaidPrice(1);
            assertFalse(VALIDATION.isValid(space, null));
        }

        @Test
        void paidIsTrue() {
            space.setIsPaid(true);
            assertFalse(VALIDATION.isValid(space, null));
        }
    }
}
