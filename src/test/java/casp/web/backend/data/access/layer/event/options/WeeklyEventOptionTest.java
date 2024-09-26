package casp.web.backend.data.access.layer.event.options;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WeeklyEventOptionTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var weeklyEventOption = TestFixture.createWeeklyEventOption();

        assertThat(TestFixture.getViolations(weeklyEventOption)).isEmpty();
    }
}
