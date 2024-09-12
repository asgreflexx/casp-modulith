package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoTrainerTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var coTrainer = TestFixture.createValidCoTrainer();

        assertThat(TestFixture.getViolations(coTrainer)).isEmpty();
        baseParticipantAssertions(coTrainer);
    }
}
