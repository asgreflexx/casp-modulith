package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamFacadeImplTest {
    @Mock
    private ExamParticipantService examParticipantService;

    @InjectMocks
    private ExamFacadeImpl examFacade;

    @Test
    void mapBaseEventToDto() {
        var dogHasHandler = TestFixture.createValidDogHasHandler();
        var examParticipant = TestFixture.createValidExamParticipant();
        examParticipant.setMemberOrHandlerId(dogHasHandler.getId());
        examParticipant.setDogHasHandler(dogHasHandler);
        var exam = examParticipant.getBaseEvent();
        when(examParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(exam.getId())).thenReturn(Set.of(examParticipant));

        var courseDto = examFacade.mapBaseEventToDto(exam);

        assertThat(courseDto.getParticipants())
                .singleElement()
                .satisfies(actual -> {
                    assertEquals(examParticipant.getId(), actual.getId());
                    assertEquals(examParticipant.getMemberOrHandlerId(), actual.getDogHasHandler().getId());
                });
    }
}
