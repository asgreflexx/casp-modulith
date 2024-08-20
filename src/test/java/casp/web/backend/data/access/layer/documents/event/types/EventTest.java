package casp.web.backend.data.access.layer.documents.event.types;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var event = createValidEvent();
        assertThat(getViolations(event)).isEmpty();
        baseAssertions(event);
    }
}
