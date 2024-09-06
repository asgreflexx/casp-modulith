package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participant.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.participant.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class BaseParticipantCustomRepositoryImplTest {
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;

    private Space space;

    @BeforeEach
    void setUp() {
        baseParticipantRepository.deleteAll();
        space = baseParticipantRepository.save(TestFixture.createValidSpace());
    }

    @Test
    void findAllByMemberOrHandlerIdAndEntityStatus() {
        assertThat(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(space.getMemberOrHandlerId(), EntityStatus.ACTIVE, space.getParticipantType())).containsExactly(space);
    }

    @Test
    void findAllByMemberOrHandlerIdAndEntityStatusNot() {
        assertThat(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(space.getMemberOrHandlerId(), EntityStatus.INACTIVE, space.getParticipantType()))
                .containsExactly(space);
    }

    @Nested
    class FindAllByMemberOrHandlerIdIn {
        @Test
        void participantWasFound() {
            assertThat(baseParticipantRepository.findAllByMemberOrHandlerIdIn(Set.of(space.getMemberOrHandlerId()), space.getParticipantType())).containsExactly(space);
        }

        @Test
        void participantNotFound() {
            assertThat(baseParticipantRepository.findAllByMemberOrHandlerIdIn(Set.of(space.getMemberOrHandlerId()), EventParticipant.PARTICIPANT_TYPE)).isEmpty();
        }
    }
}
