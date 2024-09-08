package casp.web.backend.data.access.layer.documents.event.types;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CourseTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var course = TestFixture.createValidCourse();

        assertThat(TestFixture.getViolations(course)).isEmpty();
        baseAssertions(course);
    }
}
