package casp.web.backend.data.access.layer.commons;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.EventResponse;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class BaseDocumentTest {

    protected <T extends BaseDocument> void baseAssertions(T baseEntity) {
        assertSame(EntityStatus.ACTIVE, baseEntity.getEntityStatus());
        assertNotNull(baseEntity.getId());
    }

    protected <T extends BaseParticipant> void baseParticipantAssertions(T baseParticipant) {
        baseAssertions(baseParticipant);
        assertSame(EventResponse.ACCEPTED, baseParticipant.getResponse());
    }
}
