package casp.web.backend.calendar;

import casp.web.backend.calendar.data.participants.Space;
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
class SpacesDtoValidationTest {
    private static final SpacesDtoValidation VALIDATION = new SpacesDtoValidation();
    @Mock
    private CourseDto courseDto;

    @Test
    void oneNewSpacesIsTheSameIdAsTheActualSpace() {
        var space = mock(Space.class);
        var spaceId = UUID.randomUUID();
        when(space.getId()).thenReturn(spaceId);
        when(courseDto.getNewSpaces()).thenReturn(Set.of(spaceId, UUID.randomUUID()));
        when(courseDto.getSpaces()).thenReturn(Set.of(space));

        assertFalse(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void theNewSpacesAndActualSpacesAreNotTheSame() {
        var space1 = mock(Space.class);
        var space2 = mock(Space.class);
        when(space1.getId()).thenReturn(UUID.randomUUID());
        when(space2.getId()).thenReturn(UUID.randomUUID());
        when(courseDto.getNewSpaces()).thenReturn(Set.of(UUID.randomUUID(), UUID.randomUUID()));
        when(courseDto.getSpaces()).thenReturn(Set.of(space1, space2));

        assertTrue(VALIDATION.isValid(courseDto, null));
    }
}
