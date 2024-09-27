package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.business.logic.layer.event.types.ExamService;
import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.event.types.ExamDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.ExamMapper.EXAM_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamFacadeImplTest {
    @Mock
    private ExamParticipantService examParticipantService;
    @Mock
    private CalendarService calendarService;
    @Mock
    private ExamService examService;

    @Spy
    @InjectMocks
    private ExamFacadeImpl examFacade;

    @Test
    void getById() {
        var exam = TestFixture.createExam();
        when(examService.getBaseEventById(exam.getId())).thenReturn(exam);

        var examDto = examFacade.getById(exam.getId());

        assertEquals(exam.getId(), examDto.getId());
        verify(examFacade).mapDocumentToDto(exam);
    }

    @Nested
    class Save {
        @Captor
        private ArgumentCaptor<Exam> examCaptor;
        @Captor
        private ArgumentCaptor<List<Calendar>> calendarListCaptor;
        @Captor
        private ArgumentCaptor<Set<UUID>> idSetCaptor;

        private ExamDto examDto;
        private Exam exam;

        @BeforeEach
        void setUp() {
            exam = TestFixture.createExam();
            examDto = EXAM_MAPPER.toDto(exam);
        }

        @Test
        void replaceCalendarEntries() {
            var calendarEntry = TestFixture.createCalendarEntry();
            var calendarDtoList = CALENDAR_MAPPER.toDtoList(List.of(calendarEntry));
            examDto.setCalendarEntries(calendarDtoList);

            examFacade.save(examDto);

            verify(calendarService).replaceCalendarEntries(examCaptor.capture(), calendarListCaptor.capture());
            assertEquals(exam.getId(), examCaptor.getValue().getId());
            assertThat(calendarListCaptor.getValue()).singleElement().satisfies(actual -> {
                assertEquals(calendarEntry.getEventFrom(), actual.getEventFrom());
                assertEquals(calendarEntry.getEventTo(), actual.getEventTo());
            });
        }

        @Test
        void replaceParticipants() {
            var participant = TestFixture.createExamParticipant();
            examDto.setParticipantsIdToWrite(Set.of(participant.getId()));

            examFacade.save(examDto);

            verify(examParticipantService).replaceParticipants(examCaptor.capture(), idSetCaptor.capture());
            assertEquals(exam.getId(), examCaptor.getValue().getId());
            assertThat(idSetCaptor.getValue()).singleElement().isEqualTo(participant.getId());
        }

        @Test
        void saveCourse() {
            examFacade.save(examDto);

            verify(examService).save(examCaptor.capture());
            assertEquals(exam.getId(), examCaptor.getValue().getId());
        }
    }

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

            assertThat(courseDto.getParticipantsToRead())
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
