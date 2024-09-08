package casp.web.backend.data.access.layer.documents.member;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseDocumentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest extends BaseDocumentTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = TestFixture.createValidMember();
    }

    @Test
    void happyPath() {
        assertThat(TestFixture.getViolations(member)).isEmpty();
        baseAssertions(member);
    }

    @Test
    void invalidMembershipFee() {
        var membershipFee = new MembershipFee();
        membershipFee.setPaid(true);
        member.setMembershipFees(Set.of(membershipFee));

        assertThat(TestFixture.getViolations(member)).isNotEmpty();
    }
}
