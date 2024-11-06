package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.participants.CoTrainer;
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
class CoTrainersDtoValidationTest {
    private static final CoTrainersDtoValidation VALIDATION = new CoTrainersDtoValidation();
    @Mock
    private CourseDto courseDto;

    @Test
    void oneNewCoTrainerIsTheSameIdAsTheActualCoTrainer() {
        var coTrainer = mock(CoTrainer.class);
        var coTrainerId = UUID.randomUUID();
        when(coTrainer.getId()).thenReturn(coTrainerId);
        when(courseDto.getNewCoTrainers()).thenReturn(Set.of(coTrainerId, UUID.randomUUID()));
        when(courseDto.getCoTrainers()).thenReturn(Set.of(coTrainer));

        assertFalse(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void theNewCoTrainersAndActualCoTrainersAreNotTheSame() {
        var coTrainer1 = mock(CoTrainer.class);
        var coTrainer2 = mock(CoTrainer.class);
        when(coTrainer1.getId()).thenReturn(UUID.randomUUID());
        when(coTrainer2.getId()).thenReturn(UUID.randomUUID());
        when(courseDto.getNewCoTrainers()).thenReturn(Set.of(UUID.randomUUID(), UUID.randomUUID()));
        when(courseDto.getCoTrainers()).thenReturn(Set.of(coTrainer1, coTrainer2));

        assertTrue(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void newCoTrainersAndCoTrainersAreNull() {
        assertTrue(VALIDATION.isValid(courseDto, null));
    }
}
