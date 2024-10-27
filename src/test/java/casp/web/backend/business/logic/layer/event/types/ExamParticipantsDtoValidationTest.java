package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamParticipantsDtoValidationTest {
    private static final ExamParticipantsDtoValidation VALIDATION = new ExamParticipantsDtoValidation();
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ExamDto examDto;
    @Mock
    private ExamParticipant examParticipant;
    private UUID dogHasHandlerId;

    @BeforeEach
    void setUp() {
        dogHasHandlerId = UUID.randomUUID();
        when(examParticipant.getId()).thenReturn(dogHasHandlerId);
        when(examDto.getParticipants()).thenReturn(new HashSet<>(Set.of(examParticipant)));
    }

    @Test
    void oneNewParticipantIsTheSameIdAsTheActualParticipant() {
        when(examDto.getNewParticipants()).thenReturn(Set.of(dogHasHandlerId, UUID.randomUUID()));

        assertFalse(VALIDATION.isValid(examDto, null));
    }

    @Test
    void theNewParticipantsAndTheActualParticipantsAreNotTheSame() {
        when(examDto.getNewParticipants()).thenReturn(Set.of(UUID.randomUUID(), UUID.randomUUID()));

        assertTrue(VALIDATION.isValid(examDto, null));
    }
}
