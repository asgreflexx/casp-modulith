package casp.web.backend.data.access.layer.documents.event.participant;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoTrainerTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var coTrainer = new CoTrainer();
        coTrainer.setBaseEvent(createValidEvent());
        coTrainer.setMemberOrHandlerId(UUID.randomUUID());

        assertThat(getViolations(coTrainer)).isEmpty();
        baseParticipantAssertions(coTrainer);
    }
}
