package casp.web.backend.data.access.layer.documents.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamParticipantTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var examParticipant = TestFixture.createValidExamParticipant();

        assertThat(TestFixture.getViolations(examParticipant)).isEmpty();
        baseParticipantAssertions(examParticipant);
    }
}
