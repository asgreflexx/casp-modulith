package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CourseTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var course = TestFixture.createCourse();

        assertThat(TestFixture.getViolations(course)).isEmpty();
        baseAssertions(course);
    }
}
