package casp.web.backend.common.validation;

import casp.web.backend.business.logic.layer.event.types.CourseRequiredFields;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseSpacesValidationTest {
    private static final CourseSpacesValidation VALIDATION = new CourseSpacesValidation();

    @Mock
    private CourseRequiredFields courseValidation;

    @Test
    void isValid() {
        when(courseValidation.getSpaceLimit()).thenReturn(1);
        when(courseValidation.getSpaceListSize()).thenReturn(1);

        assertTrue(VALIDATION.isValid(courseValidation, null));
    }

    @Test
    void isInvalid() {
        when(courseValidation.getSpaceLimit()).thenReturn(1);
        when(courseValidation.getSpaceListSize()).thenReturn(2);

        assertFalse(VALIDATION.isValid(courseValidation, null));
    }
}
