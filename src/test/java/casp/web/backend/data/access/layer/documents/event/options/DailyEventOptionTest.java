package casp.web.backend.data.access.layer.documents.event.options;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DailyEventOptionTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var dailyEventOption = TestFixture.createValidDailyEventOption();

        assertThat(TestFixture.getViolations(dailyEventOption)).isEmpty();
    }
}
