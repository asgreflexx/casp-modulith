package casp.web.backend.data.access.layer.documents.member;


import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipFeeTest extends BaseEntityTest {

    private MembershipFee membershipFee;

    @BeforeEach
    void setUp() {
        membershipFee = new MembershipFee();
    }

    @Nested
    class Valid {
        @Test
        void notPaid() {
            assertThat(TestFixture.getViolations(membershipFee)).isEmpty();
        }

        @Test
        void paid() {
            membershipFee.setPaid(true);
            membershipFee.setPaidDate(LocalDate.now());
            membershipFee.setPaidPrice(10.0);
            assertThat(TestFixture.getViolations(membershipFee)).isEmpty();
        }
    }
}
