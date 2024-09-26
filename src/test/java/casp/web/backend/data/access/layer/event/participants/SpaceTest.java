package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpaceTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var space = TestFixture.createSpace();

        assertThat(TestFixture.getViolations(space)).isEmpty();
        baseParticipantAssertions(space);
    }
}
