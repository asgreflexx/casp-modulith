package casp.web.backend.data.access.layer.documents.event.participant;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExamParticipantTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var examParticipant = new ExamParticipant();
        examParticipant.setMemberOrHandlerId(UUID.randomUUID());
        examParticipant.setBaseEvent(TestFixture.createValidEvent());

        assertThat(TestFixture.getViolations(examParticipant)).isEmpty();
        baseParticipantAssertions(examParticipant);
    }
}
