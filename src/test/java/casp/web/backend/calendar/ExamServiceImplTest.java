package casp.web.backend.calendar;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.Exam;
import casp.web.backend.calendar.data.ExamRepository;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.calendar.CalendarFixture.ZONE_ID;
import static casp.web.backend.calendar.CalendarFixture.createLocalDate;
import static casp.web.backend.calendar.CalendarFixture.createNewCalendarEntryDto;
import static casp.web.backend.calendar.ExamMapper.EXAM_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
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

    @Captor
    private ArgumentCaptor<Exam> examCaptor;

    private Exam exam;

    private ExamServiceImpl examService;

    @BeforeEach
    void setUp() {
        exam = new Exam();
        examService = new ExamServiceImpl(examRepository);
        examService.setZoneId(ZONE_ID);
        examService.setMemberReferenceRepository(memberReferenceRepository);
        examService.setDogHasHandlerReferenceRepository(dogHasHandlerReferenceRepository);
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

    @Nested
    class Save {
        private ExamDto examDto;
        private NewCalendarEntryDto newCalendarEntryDto;

        @BeforeEach
        void setUp() {
            newCalendarEntryDto = createNewCalendarEntryDto();
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
                        assertEquals(newCalendarEntryDto.getEntryFromODT(), ce.getEntryFromODT());
                        assertEquals(newCalendarEntryDto.getEntryToODT(), ce.getEntryToODT());
                    });
        }

        @Test
        void setRecurrenceOption() {
            var memberReference = mockMember();
            examDto.setMemberId(memberReference.getId());
            examDto.setNewCalendarEntry(null);
            var daily = new DailyRecurrenceOption();
            daily.setStartTime(LocalTime.of(1, 0, 0));
            daily.setEndTime(LocalTime.of(3, 0, 0));
            daily.setStartRecurrence(createLocalDate(0));
            daily.setEndRecurrence(createLocalDate(2));
            examDto.setRecurrenceOption(daily);

            examService.save(examDto);

            var actualCourse = getExamSaved();
            assertThat(actualCourse.getCalendarEntries()).hasSize(3);
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

    @Nested
    class Participants {
        private ExamDto examDto;

        @BeforeEach
        void setUp() {
            var newCalendarEntryDto = createNewCalendarEntryDto();
            examDto = new ExamDto();
            examDto.setMemberId(mockMember().getId());
            examDto.setNewCalendarEntry(newCalendarEntryDto);
        }

        @Test
        void addNewParticipantToNewExam() {
            var newParticipant = ReferenceTestFixture.createDogHasHandlerReference("dog", "new", "participant");
            when(dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(newParticipant.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(newParticipant));
            when(examRepository.findOneByIdAndEntityStatus(examDto.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());
            var expectedParticipant = new ExamParticipant(newParticipant);
            examDto.getParticipantIds().add(newParticipant.getId());

            examService.save(examDto);

            var actualCourse = getExamSaved();
            assertThat(actualCourse.getParticipants())
                    .singleElement()
                    .isEqualTo(expectedParticipant);
        }

        @Test
        void addNewParticipantToEmptyList() {
            var newParticipant = ReferenceTestFixture.createDogHasHandlerReference("dog", "new", "participant");
            when(dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(newParticipant.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(newParticipant));
            when(examRepository.findOneByIdAndEntityStatus(examDto.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(EXAM_MAPPER.toSource(examDto)));
            var expectedParticipant = new ExamParticipant(newParticipant);
            examDto.getParticipantIds().add(newParticipant.getId());

            examService.save(examDto);

            var actualCourse = getExamSaved();
            assertThat(actualCourse.getParticipants())
                    .singleElement()
                    .isEqualTo(expectedParticipant);
        }

        @Test
        void addExistingParticipantToEvent() {
            var existingParticipant = ReferenceTestFixture.createDogHasHandlerReference("dog", "existing", "participant");
            var sourceExam = EXAM_MAPPER.toSource(examDto);
            sourceExam.addParticipants(Set.of(new ExamParticipant(existingParticipant)));
            when(examRepository.findOneByIdAndEntityStatus(examDto.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(sourceExam));
            var expectedParticipant = new ExamParticipant(existingParticipant);
            examDto.getParticipantIds().add(expectedParticipant.getId());

            examService.save(examDto);

            var actualCourse = getExamSaved();
            assertThat(actualCourse.getParticipants())
                    .singleElement()
                    .isEqualTo(expectedParticipant);
            verify(dogHasHandlerReferenceRepository, never()).findOneByIdAndEntityStatus(existingParticipant.getId(), EntityStatus.ACTIVE);
        }

        @Test
        void replaceExistingParticipantWithNewParticipant() {
            var existingParticipant = ReferenceTestFixture.createDogHasHandlerReference("dog", "existing", "participant");
            var sourceExam = EXAM_MAPPER.toSource(examDto);
            sourceExam.addParticipants(Set.of(new ExamParticipant(existingParticipant)));
            when(examRepository.findOneByIdAndEntityStatus(examDto.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(sourceExam));
            var newParticipant = ReferenceTestFixture.createDogHasHandlerReference("dog", "new", "participant");
            when(dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(newParticipant.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(newParticipant));
            var expectedParticipant = new ExamParticipant(newParticipant);
            examDto.getParticipantIds().add(expectedParticipant.getId());

            examService.save(examDto);

            var actualCourse = getExamSaved();
            assertThat(actualCourse.getParticipants())
                    .singleElement()
                    .isEqualTo(expectedParticipant);
        }
    }

    @Test
    void getExamsByDogHasHandlerId() {
        var dogHasHandlerId = UUID.randomUUID();
        when(examRepository.findAllByParticipantId(dogHasHandlerId, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(exam)));

        var examDtoPage = examService.getExamsByDogHasHandlerId(dogHasHandlerId, Pageable.unpaged());

        assertThat(examDtoPage)
                .containsExactly(EXAM_MAPPER.toTarget(exam));
    }

    @Nested
    class GetOneById {
        @Test
        void exist() {
            when(examRepository.findOneByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));

            var examDto = examService.getOneById(exam.getId());

            assertEquals(exam.getId(), examDto.getId());
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(examRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.getOneById(id));
        }
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
