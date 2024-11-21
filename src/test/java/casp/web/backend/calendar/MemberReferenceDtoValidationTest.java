package casp.web.backend.calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberReferenceDtoValidationTest {
    private static final MemberReferenceDtoValidation VALIDATION = new MemberReferenceDtoValidation();

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CourseDto courseDto;

    @Test
    void newMemberIdAndMemberAreEmpty() {
        when(courseDto.getNewMemberId()).thenReturn(null);
        when(courseDto.getMember()).thenReturn(null);

        assertFalse(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void newMemberIdIsNotEmpty() {
        when(courseDto.getNewMemberId()).thenReturn(UUID.randomUUID());
        when(courseDto.getMember()).thenReturn(null);

        assertTrue(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void memberIsNotEmpty() {
        when(courseDto.getNewMemberId()).thenReturn(null);
        when(courseDto.getMember().getId()).thenReturn(UUID.randomUUID());

        assertTrue(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void newMemberIdIsNotEqualToMemberId() {
        when(courseDto.getNewMemberId()).thenReturn(UUID.randomUUID());
        when(courseDto.getMember().getId()).thenReturn(UUID.randomUUID());

        assertTrue(VALIDATION.isValid(courseDto, null));
    }

    @Test
    void newMemberIdIsEqualToMemberId() {
        var memberId = UUID.randomUUID();
        when(courseDto.getNewMemberId()).thenReturn(memberId);
        when(courseDto.getMember().getId()).thenReturn(memberId);

        assertFalse(VALIDATION.isValid(courseDto, null));
    }
}
