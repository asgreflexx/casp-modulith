package casp.web.backend.calendar;

import casp.web.backend.calendar.data.participants.EventParticipant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipantsDtoValidationTest {
    private static final EventParticipantsDtoValidation VALIDATION = new EventParticipantsDtoValidation();
    @Mock
    private EventDto eventDto;

    @Test
    void oneNewParticipantIsTheSameIdAsTheActualParticipant() {
        var participant = mock(EventParticipant.class);
        var participantId = UUID.randomUUID();
        when(participant.getId()).thenReturn(participantId);
        when(eventDto.getNewParticipants()).thenReturn(Set.of(participantId, UUID.randomUUID()));
        when(eventDto.getParticipants()).thenReturn(Set.of(participant));

        assertFalse(VALIDATION.isValid(eventDto, null));
    }

    @Test
    void theNewParticipantsAndActualParticipantsAreNotTheSame() {
        var participant1 = mock(EventParticipant.class);
        var participant2 = mock(EventParticipant.class);
        when(participant1.getId()).thenReturn(UUID.randomUUID());
        when(participant2.getId()).thenReturn(UUID.randomUUID());
        when(eventDto.getNewParticipants()).thenReturn(Set.of(UUID.randomUUID(), UUID.randomUUID()));
        when(eventDto.getParticipants()).thenReturn(Set.of(participant1, participant2));

        assertTrue(VALIDATION.isValid(eventDto, null));
    }
}
