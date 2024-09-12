package casp.web.backend.data.access.layer.member;


import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipFeeTest extends BaseDocumentTest {
    @Nested
    class Validate {
        @Test
        void notPaid() {
            var membershipFee = new MembershipFee();
            assertThat(TestFixture.getViolations(membershipFee)).isEmpty();
        }

        @Test
        void paid() {
            var membershipFee = TestFixture.createValidMembershipFee();
            assertThat(TestFixture.getViolations(membershipFee)).isEmpty();
        }
    }
}
