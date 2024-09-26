package casp.web.backend.data.access.layer.event.options;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DailyEventOptionTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var dailyEventOption = TestFixture.createDailyEventOption();

        assertThat(TestFixture.getViolations(dailyEventOption)).isEmpty();
    }
}
