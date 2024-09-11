package casp.web.backend.data.access.layer.documents.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpaceTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var space = TestFixture.createValidSpace();

        assertThat(TestFixture.getViolations(space)).isEmpty();
        baseParticipantAssertions(space);
    }
}
