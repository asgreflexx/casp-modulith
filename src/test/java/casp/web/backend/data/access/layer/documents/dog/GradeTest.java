package casp.web.backend.data.access.layer.documents.dog;


import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GradeTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var grade = createValidGrade();
        assertThat(getViolations(grade)).isEmpty();
    }
}
