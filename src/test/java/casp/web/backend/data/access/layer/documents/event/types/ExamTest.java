package casp.web.backend.data.access.layer.documents.event.types;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var exam = TestFixture.createValidExam();

        assertThat(TestFixture.getViolations(exam)).isEmpty();
        baseAssertions(exam);
    }
}
