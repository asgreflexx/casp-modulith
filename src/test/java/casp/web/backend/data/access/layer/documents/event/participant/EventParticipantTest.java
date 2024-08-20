package casp.web.backend.data.access.layer.documents.event.participant;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventParticipantTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var eventParticipant = new EventParticipant();
        eventParticipant.setMemberOrHandlerId(UUID.randomUUID());
        eventParticipant.setBaseEvent(createValidEvent());

        assertThat(getViolations(eventParticipant)).isEmpty();
        baseParticipantAssertions(eventParticipant);
    }
}
