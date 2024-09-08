package casp.web.backend.data.access.layer.documents.event.participant;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventParticipantTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var eventParticipant = TestFixture.createValidEventParticipant();

        assertThat(TestFixture.getViolations(eventParticipant)).isEmpty();
        baseParticipantAssertions(eventParticipant);
    }
}
