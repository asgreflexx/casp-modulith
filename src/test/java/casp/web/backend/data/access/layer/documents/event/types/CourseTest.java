package casp.web.backend.data.access.layer.documents.event.types;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CourseTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var course = new Course();
        course.setName("Course Name");
        course.setMemberId(UUID.randomUUID());
        course.setMember(createValidMember());

        assertThat(getViolations(course)).isEmpty();
        baseAssertions(course);
    }
}
