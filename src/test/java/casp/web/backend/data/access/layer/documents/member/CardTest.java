package casp.web.backend.data.access.layer.documents.member;

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
        card.setMember(createValidMember());

        assertThat(getViolations(card)).isEmpty();
        baseAssertions(card);
    }
}
