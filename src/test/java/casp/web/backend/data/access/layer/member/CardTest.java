package casp.web.backend.data.access.layer.member;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var card = TestFixture.createCard();

        assertThat(TestFixture.getViolations(card)).isEmpty();
        baseAssertions(card);
    }
}
