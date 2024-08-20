package casp.web.backend.data.access.layer.documents.event.types;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExamTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var exam = new Exam();
        exam.setName("Exam 1");
        exam.setMemberId(UUID.randomUUID());
        exam.setJudgeName("Judge");
        exam.setMember(createValidMember());

        assertThat(getViolations(exam)).isEmpty();
        baseAssertions(exam);
    }
}
