package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.types.Course;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseSpacesValidationTest {
    private static final CourseSpacesValidation VALIDATION = new CourseSpacesValidation();

    @Mock(answer = RETURNS_DEEP_STUBS)
    private Course course;

    @Test
    void isValid() {
        when(course.getSpaceLimit()).thenReturn(1);
        when(course.getSpaces().size()).thenReturn(1);

        assertTrue(VALIDATION.isValid(course, null));
    }

    @Test
    void isInvalid() {
        when(course.getSpaceLimit()).thenReturn(1);
        when(course.getSpaces().size()).thenReturn(2);

        assertFalse(VALIDATION.isValid(course, null));
    }
}
