package casp.web.backend.data.access.layer.dog;


import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import casp.web.backend.data.access.layer.enumerations.GradeType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class GradeTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var grade = new Grade();
        grade.setName("grade");
        grade.setType(GradeType.BH1);
        grade.setPoints(1);
        grade.setExamDate(LocalDate.now());
        assertThat(TestFixture.getViolations(grade)).isEmpty();
    }
}
