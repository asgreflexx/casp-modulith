package casp.web.backend.data.access.layer.documents.event.participant;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventParticipantTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var eventParticipant = TestFixture.createValidEventParticipant();

        assertThat(TestFixture.getViolations(eventParticipant)).isEmpty();
        baseParticipantAssertions(eventParticipant);
    }
}
