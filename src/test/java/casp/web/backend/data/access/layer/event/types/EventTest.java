package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var event = TestFixture.createEvent();
        assertThat(TestFixture.getViolations(event)).isEmpty();
        baseAssertions(event);
    }
}
