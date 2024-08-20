package casp.web.backend.data.access.layer.documents.event.participant;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SpaceTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var space = new Space();
        space.setMemberOrHandlerId(UUID.randomUUID());
        space.setBaseEvent(createValidEvent());

        assertThat(getViolations(space)).isEmpty();
        baseParticipantAssertions(space);
    }
}
