package casp.web.backend.data.access.layer.documents.event.options;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WeeklyEventOptionTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var weeklyEventOption = TestFixture.createValidWeeklyEventOption();

        assertThat(TestFixture.getViolations(weeklyEventOption)).isEmpty();
    }
}
