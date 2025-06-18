package casp.web.backend.calendar;


import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.Exam;
import casp.web.backend.calendar.data.ExamRepository;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {
    @Mock
    private ExamRepository examRepository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Mock
    private BaseEventMigrationService migrationService;

    @Captor
    private ArgumentCaptor<Exam> examCaptor;

    private Exam exam;

    @InjectMocks
    private ExamServiceImpl examService;

    @BeforeEach
    void setUp() {
        exam = new Exam();
    }

    @Test
    void deleteBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(examRepository.findAllByMemberIdAndNotDeleted(memberId)).thenReturn(Set.of(exam));

        examService.deleteBaseEventsByMemberId(memberId);

        verify(examRepository).save(examCaptor.capture());
        assertThat(examCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(examRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Set.of(exam));

        examService.deactivateBaseEventsByMemberId(memberId);

        verify(examRepository).save(examCaptor.capture());
        assertThat(examCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.INACTIVE);
    }

    @Test
    void activateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(examRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.INACTIVE)).thenReturn(Set.of(exam));

        examService.activateBaseEventsByMemberId(memberId);

        verify(examRepository).save(examCaptor.capture());
        assertThat(examCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void migrateDataToV2() {
        var examSet = Set.of(exam);
        when(migrationService.mapToExamV2()).thenReturn(examSet);

        examService.migrateDataToV2();

        verify(examRepository).deleteAll();
        verify(examRepository).saveAll(examSet);
    }

    @Test
    void getCalendarEntries() {
        var from = LocalDateTime.now().minusDays(1);
        var to = from.plusDays(1);
        var calendarEntry1 = new CalendarEntry(from.minusHours(1), from);
        var calendarEntry2 = new CalendarEntry(from, to);
        var calendarEntry3 = new CalendarEntry(to, to.plusHours(1));
        exam.setCalendarEntries(new ArrayList<>(List.of(calendarEntry1, calendarEntry2, calendarEntry3)));
        when(examRepository.findAllBetweenFromAndTo(from, to)).thenReturn(Stream.of(exam));

        var calendarEntryDtoStream = examService.getCalendarEntriesBetweenFromAndTo(from, to);

        assertThat(calendarEntryDtoStream)
                .singleElement()
                .satisfies(ce -> {
                    assertEquals(calendarEntry2.getEntryFrom(), ce.getEntryFrom());
                    assertEquals(calendarEntry2.getEntryTo(), ce.getEntryTo());
                    assertSame(exam.getEventType(), ce.getEventType());
                });
    }

    @Nested
    class GetOneByIdAndCalendarEntryId {

        private CalendarEntry calendarEntry;

        @BeforeEach
        void setUp() {
            calendarEntry = new CalendarEntry(LocalDateTime.MIN, LocalDateTime.MAX);
            exam.addCalendarEntry(calendarEntry);
            exam.addCalendarEntry(new CalendarEntry(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        }

        @Test
        void examExist() {
            when(examRepository.findOneByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));

            var courseDto = examService.getOneByIdAndCalendarEntryId(exam.getId(), calendarEntry.getId());

            assertEquals(exam.getId(), courseDto.getId());
            assertThat(courseDto.getCalendarEntries())
                    .singleElement()
                    .satisfies(ce -> {
                        assertEquals(calendarEntry.getEntryFrom(), ce.getEntryFrom());
                        assertEquals(calendarEntry.getEntryTo(), ce.getEntryTo());
                    });
        }

        @Test
        void examDoesNotExist() {
            var id = UUID.randomUUID();
            when(examRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.getOneByIdAndCalendarEntryId(id, calendarEntry.getId()));
        }

        @Test
        void calendarEntryDoesNotExist() {
            when(examRepository.findOneByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));

            assertThrows(NoSuchElementException.class, () -> examService.getOneByIdAndCalendarEntryId(exam.getId(), UUID.randomUUID()));
        }
    }

    @Nested
    class Save {
        private ExamDto examDto;
        private NewCalendarEntryDto newCalendarEntryDto;

        @BeforeEach
        void setUp() {
            newCalendarEntryDto = new NewCalendarEntryDto();
            newCalendarEntryDto.setEntryFrom(LocalDateTime.MIN);
            newCalendarEntryDto.setEntryTo(LocalDateTime.MAX);
            examDto = new ExamDto();
            examDto.setNewCalendarEntry(newCalendarEntryDto);
        }

        @Test
        void setCalendarEntry() {
            var memberReference = mockMember();
            examDto.setMemberId(memberReference.getId());

            examService.save(examDto);

            var actualCourse = getExamSaved();
            assertThat(actualCourse.getCalendarEntries())
                    .singleElement()
                    .satisfies(ce -> {
                        assertEquals(newCalendarEntryDto.getEntryFrom(), ce.getEntryFrom());
                        assertEquals(newCalendarEntryDto.getEntryTo(), ce.getEntryTo());
                    });
            assertEquals(newCalendarEntryDto.getEntryFrom(), actualCourse.getMinTime());
            assertEquals(newCalendarEntryDto.getEntryTo(), actualCourse.getMaxTime());
        }

        @Test
        void setRecurrenceOption() {
            var memberReference = mockMember();
            examDto.setMemberId(memberReference.getId());
            examDto.setNewCalendarEntry(null);
            var daily = new DailyRecurrenceOption();
            daily.setStartTime(LocalTime.of(1, 0, 0));
            daily.setEndTime(LocalTime.of(3, 0, 0));
            daily.setStartRecurrence(LocalDate.of(2024, 10, 1));
            daily.setEndRecurrence(LocalDate.of(2024, 10, 3));
            examDto.setRecurrenceOption(daily);
            var minTime = LocalDateTime.of(daily.getStartRecurrence(), daily.getStartTime());
            var maxTime = LocalDateTime.of(daily.getEndRecurrence(), daily.getEndTime());

            examService.save(examDto);

            var actualCourse = getExamSaved();
            assertThat(actualCourse.getCalendarEntries()).hasSize(3);
            assertEquals(minTime, actualCourse.getMinTime());
            assertEquals(maxTime, actualCourse.getMaxTime());
        }

        @Test
        void setNewMember() {
            var memberReference = mockMember();
            examDto.setMemberId(memberReference.getId());

            examService.save(examDto);

            assertEquals(memberReference, getExamSaved().getMember());
        }

        @Test
        void memberDoesNotExist() {
            var newMemberId = UUID.randomUUID();
            examDto.setMemberId(newMemberId);
            when(memberReferenceRepository.findOneByIdAndEntityStatus(newMemberId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.save(examDto));
        }

        @Test
        void addParticipant() {
            var memberReference = mockMember();
            examDto.setMemberId(memberReference.getId());
            var dogHasHandlerReference = new DogHasHandlerReference();
            dogHasHandlerReference.setId(UUID.randomUUID());
            dogHasHandlerReference.setDog(new DogReference());
            dogHasHandlerReference.setMember(ReferenceTestFixture.createMemberReference());
            var participant = new ExamParticipant(dogHasHandlerReference);
            examDto.setNewParticipants(Set.of(participant.getId()));
            when(dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(participant.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandlerReference));

            examService.save(examDto);

            var actualCourse = getExamSaved();
            assertThat(actualCourse.getParticipants()).singleElement().isEqualTo(participant);
        }

        private Exam getExamSaved() {
            verify(examRepository).save(examCaptor.capture());
            return examCaptor.getValue();
        }

        private MemberReference mockMember() {
            var memberReference = ReferenceTestFixture.createMemberReference();
            when(memberReferenceRepository.findOneByIdAndEntityStatus(memberReference.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(memberReference));
            return memberReference;
        }
    }

    @Nested
    class DeleteById {
        @Test
        void exist() {
            when(examRepository.findOneByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));

            examService.deleteById(exam.getId());

            verify(examRepository).save(examCaptor.capture());
            assertThat(examCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.DELETED);
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(examRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.deleteById(id));
        }
    }
}
