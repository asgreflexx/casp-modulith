package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamFacadeImplTest {
    @Mock
    private ExamParticipantService examParticipantService;

    @InjectMocks
    private ExamFacadeImpl examFacade;

    @Nested
    class MapDocumentToDto {
        @Test
        void mapParticipant() {
            var dogHasHandler = TestFixture.createDogHasHandler();
            var examParticipant = TestFixture.createExamParticipant();
            examParticipant.setMemberOrHandlerId(dogHasHandler.getId());
            examParticipant.setDogHasHandler(dogHasHandler);
            var exam = examParticipant.getBaseEvent();
            when(examParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(exam.getId())).thenReturn(Set.of(examParticipant));

            var courseDto = examFacade.mapDocumentToDto(exam);

            assertThat(courseDto.getParticipants())
                    .singleElement()
                    .satisfies(actual -> {
                        assertEquals(examParticipant.getId(), actual.getId());
                        assertEquals(examParticipant.getMemberOrHandlerId(), actual.getDogHasHandler().getId());
                    });
        }

        @Test
        void mapDailyEventOption() {
            var dailyEventOption = TestFixture.createDailyEventOption();
            var exam = TestFixture.createExam();
            exam.setDailyOption(dailyEventOption);

            var examDto = examFacade.mapDocumentToDto(exam);

            assertEquals(dailyEventOption.getOptionType(), examDto.getOption().getOptionType());
        }

        @Test
        void mapWeeklyEventOption() {
            var weeklyEventOption = TestFixture.createWeeklyEventOption();
            var exam = TestFixture.createExam();
            exam.setWeeklyOption(weeklyEventOption);

            var examDto = examFacade.mapDocumentToDto(exam);

            assertEquals(weeklyEventOption.getOptionType(), examDto.getOption().getOptionType());
        }

        @Test
        void mapWrongBaseEventType() {
            var event = TestFixture.createEvent();

            assertThrows(IllegalArgumentException.class, () -> examFacade.mapDocumentToDto(event));
        }
    }
}
