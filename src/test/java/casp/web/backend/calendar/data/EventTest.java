package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.EventParticipant;
import casp.web.backend.common.enums.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventTest {

    private Event event;

    private static EventParticipant mockParticipant(EntityStatus entityStatus) {
        var participant = mock(EventParticipant.class, Answers.RETURNS_DEEP_STUBS);
        when(participant.getMember().getEntityStatus()).thenReturn(entityStatus);
        return participant;
    }

    @BeforeEach
    void setUp() {
        event = new Event();
    }

    @Test
    void getNotDeletedParticipants() {
        var active = mockParticipant(EntityStatus.ACTIVE);
        var deleted = mockParticipant(EntityStatus.DELETED);
        event.setParticipants(Set.of(active, deleted));

        assertThat(event.getParticipants())
                .singleElement()
                .isEqualTo(active);
    }
}
