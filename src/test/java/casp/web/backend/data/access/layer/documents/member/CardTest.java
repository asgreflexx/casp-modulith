package casp.web.backend.data.access.layer.documents.member;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CardTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var card = new Card();
        card.setCode("code");
        card.setMemberId(UUID.randomUUID());
        card.setMember(TestFixture.createValidMember());

        assertThat(TestFixture.getViolations(card)).isEmpty();
        baseAssertions(card);
    }
}
