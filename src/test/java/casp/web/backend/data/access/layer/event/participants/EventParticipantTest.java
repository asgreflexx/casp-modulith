package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventParticipantTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var eventParticipant = TestFixture.createEventParticipant();

        assertThat(TestFixture.getViolations(eventParticipant)).isEmpty();
        baseParticipantAssertions(eventParticipant);
    }
}
