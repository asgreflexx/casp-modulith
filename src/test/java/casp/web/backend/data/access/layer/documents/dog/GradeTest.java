package casp.web.backend.data.access.layer.documents.dog;


import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import casp.web.backend.data.access.layer.documents.enumerations.GradeType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class GradeTest extends BaseEntityTest {
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
