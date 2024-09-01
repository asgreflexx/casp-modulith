package casp.web.backend.data.access.layer.documents.event.participant;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamParticipantTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var examParticipant = TestFixture.createValidExamParticipant();

        assertThat(TestFixture.getViolations(examParticipant)).isEmpty();
        baseParticipantAssertions(examParticipant);
    }
}
