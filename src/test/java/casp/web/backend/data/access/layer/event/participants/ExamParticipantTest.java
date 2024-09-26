package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamParticipantTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var examParticipant = TestFixture.createExamParticipant();

        assertThat(TestFixture.getViolations(examParticipant)).isEmpty();
        baseParticipantAssertions(examParticipant);
    }
}
