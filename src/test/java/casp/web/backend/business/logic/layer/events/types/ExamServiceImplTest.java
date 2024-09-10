package casp.web.backend.business.logic.layer.events.types;


import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.participants.ExamParticipantService;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.events.ExamMapper.EXAM_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    private CalendarService calendarService;
    @Mock
    private ExamParticipantService participantService;
    @Mock
    private BaseEventRepository eventRepository;

    @InjectMocks
    private ExamServiceImpl examService;

    private Exam exam;
    private Calendar calendarEntry;
    private List<Calendar> calendarEntries;
    private Set<ExamParticipant> participants;

    @BeforeEach
    void setUp() {
        exam = TestFixture.createValidExam();
        calendarEntry = TestFixture.createValidCalendarEntry(exam);
        calendarEntries = List.of(calendarEntry);
        participants = Set.of(TestFixture.createValidExamParticipant(exam));
    }

    @Test
    void saveBaseEventDto() {
        var eventDto = EXAM_MAPPER.toDto(exam);
        eventDto.setCalendarEntries(calendarEntries);
        eventDto.setParticipants(participants);
        when(calendarService.replaceCalendarEntriesFromEvent(exam, calendarEntries.getFirst())).thenReturn(calendarEntries);
        when(participantService.saveParticipants(eventDto.getParticipants(), exam)).thenReturn(eventDto.getParticipants());
        when(eventRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var actualExam = examService.saveBaseEventDto(eventDto);

        assertSame(calendarEntry.getEventTo(), actualExam.getMaxLocalDateTime());
        assertSame(calendarEntry.getEventFrom(), actualExam.getMinLocalDateTime());
        assertEquals(calendarEntries, actualExam.getCalendarEntries());
        assertEquals(participants.size(), actualExam.getParticipantsSize());
        assertEquals(participants, actualExam.getParticipants());
    }

    @Test
    void deleteBaseEventById() {
        when(eventRepository.findByIdAndEntityStatusNot(exam.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(exam));

        examService.deleteBaseEventById(exam.getId());

        verify(participantService).deleteParticipantsByBaseEventId(exam.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.DELETED, exam.getEntityStatus());
    }

    @Test
    void createNewBaseEventWithOneCalendarEntry() {
        var expectedFrom = LocalDateTime.now(ZoneId.systemDefault());
        var expectedTo = expectedFrom.plusHours(1);
        var examDto = examService.createNewBaseEventWithOneCalendarEntry();

        assertThat(examDto.getCalendarEntries())
                .singleElement()
                .satisfies(calendar -> {
                    assertThat(calendar.getEventFrom()).isCloseTo(expectedFrom, within(3, ChronoUnit.SECONDS));
                    assertThat(calendar.getEventTo()).isCloseTo(expectedTo, within(3, ChronoUnit.SECONDS));
                });
    }

    @Test
    void getBaseEventsAsPage() {
        var page = new PageImpl<BaseEvent>(List.of(exam));
        var year = LocalDate.now().getYear();
        when(eventRepository.findAllByYearAndEventType(year, exam.getEventType(), Pageable.unpaged())).thenReturn(page);

        assertThat(examService.getBaseEventsAsPage(year, Pageable.unpaged()).getContent()).
                singleElement()
                .isEqualTo(exam);
    }

    @Test
    void deleteBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, exam.getEventType())).thenReturn(Set.of(exam));

        examService.deleteBaseEventsByMemberId(memberId);

        verify(participantService).deleteParticipantsByBaseEventId(exam.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.DELETED, exam.getEntityStatus());
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, exam.getEventType())).thenReturn(Set.of(exam));

        examService.deactivateBaseEventsByMemberId(memberId);

        verify(participantService).deactivateParticipantsByBaseEventId(exam.getId());
        verify(calendarService).deactivateCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.INACTIVE, exam.getEntityStatus());
    }

    @Test
    void activateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, exam.getEventType())).thenReturn(Set.of(exam));

        examService.activateBaseEventsByMemberId(memberId);

        verify(participantService).activateParticipantsByBaseEventId(exam.getId());
        verify(calendarService).activateCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.ACTIVE, exam.getEntityStatus());
    }

    @Nested
    class GetBaseEventDtoById {
        @Test
        void eventExist() {
            when(eventRepository.findByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));
            when(calendarService.getCalendarEntriesByBaseEvent(exam)).thenReturn(calendarEntries);
            when(participantService.getParticipantsByBaseEventId(exam.getId())).thenReturn(participants);

            var examDto = examService.getBaseEventDtoById(exam.getId());

            assertEquals(calendarEntries, examDto.getCalendarEntries());
            assertSame(participants, examDto.getParticipants());
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.getBaseEventDtoById(id));
        }
    }
}
