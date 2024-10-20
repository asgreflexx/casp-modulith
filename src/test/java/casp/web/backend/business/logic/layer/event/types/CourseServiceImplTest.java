package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.DailyRecurrenceOption;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Mock
    private BaseEventMigrationService migrationService;
    @Captor
    private ArgumentCaptor<Course> courseCaptor;

    private Course course;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        course = new Course();
    }

    @Test
    void getAllByYear() {
        var coursePage = new PageImpl<>(List.of(course));
        when(courseRepository.findAllByYear(2024, Pageable.unpaged())).thenReturn(coursePage);

        var actualCoursePage = courseService.getAllByYear(2024, Pageable.unpaged());

        assertThat(actualCoursePage)
                .singleElement()
                .satisfies(c -> assertEquals(course.getId(), c.getId()));
    }

    @Test
    void deleteBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(courseRepository.findAllByMemberIdAndNotDeleted(memberId)).thenReturn(Set.of(course));

        courseService.deleteBaseEventsByMemberId(memberId);

        verify(courseRepository).save(courseCaptor.capture());
        assertThat(courseCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(courseRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Set.of(course));

        courseService.deactivateBaseEventsByMemberId(memberId);

        verify(courseRepository).save(courseCaptor.capture());
        assertThat(courseCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.INACTIVE);
    }

    @Test
    void activateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(courseRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.INACTIVE)).thenReturn(Set.of(course));

        courseService.activateBaseEventsByMemberId(memberId);

        verify(courseRepository).save(courseCaptor.capture());
        assertThat(courseCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void migrateDataToV2() {
        var courseSet = Set.of(course);
        when(migrationService.mapToCourseV2()).thenReturn(courseSet);

        courseService.migrateDataToV2();

        verify(courseRepository).deleteAll();
        verify(courseRepository).saveAll(courseSet);

    }

    @Nested
    class RemoveSpace {
        @Test
        void courseExist() {
            var dogHasHandler = new DogHasHandlerReference();
            dogHasHandler.setMember(new MemberReference());
            dogHasHandler.setDog(new DogReference());
            var space = new Space(dogHasHandler);
            course.setSpaces(Set.of(space));

            when(courseRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            courseService.removeSpace(course.getId(), space.getId());

            verify(courseRepository).save(courseCaptor.capture());

            assertThat(courseCaptor.getValue().getSpaces())
                    .isEmpty();
        }

        @Test
        void courseDoesNotExist() {
            var id = UUID.randomUUID();
            var idSpace = UUID.randomUUID();
            when(courseRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.removeSpace(id, idSpace));
        }
    }

    @Nested
    class SaveSpace {
        @Test
        void courseExist() {
            var member = new MemberReference();
            member.setEmail("mail@mail");
            var dogHasHandler = new DogHasHandlerReference();
            dogHasHandler.setMember(member);
            dogHasHandler.setDog(new DogReference());
            var space = new Space(dogHasHandler);
            course.setSpaces(Set.of(space));

            when(courseRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            space.setNote("spaceChanged");
            courseService.saveSpace(course.getId(), space);

            verify(courseRepository).save(courseCaptor.capture());

            assertThat(courseCaptor.getValue().getSpaces())
                    .singleElement()
                    .satisfies(s -> assertEquals(space.getNote(), s.getNote()));
        }

        @Test
        void courseDoesNotExist() {
            var id = UUID.randomUUID();
            var space = new Space();
            when(courseRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.saveSpace(id, space));
        }
    }

    @Nested
    class GetEmailsByCourseId {
        @Test
        void exist() {
            var member = new MemberReference();
            member.setEmail("mail@mail");
            var dogHasHandler = new DogHasHandlerReference();
            dogHasHandler.setMember(member);
            dogHasHandler.setDog(new DogReference());
            var space = new Space(dogHasHandler);
            course.setSpaces(Set.of(space));
            when(courseRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            var emailSet = courseService.getEmailsByCourseId(course.getId());

            assertThat(emailSet)
                    .singleElement()
                    .isEqualTo(member.getEmail());
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(courseRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.getEmailsByCourseId(id));
        }
    }

    @Nested
    class DeleteById {
        @Test
        void exist() {
            when(courseRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            courseService.deleteById(course.getId());

            verify(courseRepository).save(courseCaptor.capture());
            assertThat(courseCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.DELETED);
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(courseRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.deleteById(id));
        }
    }

    @Nested
    class Save {
        private CourseDto courseDto;
        private CalendarEntry calendarEntry;

        @BeforeEach
        void setUp() {
            calendarEntry = new CalendarEntry();
            calendarEntry.setEntryFrom(LocalDateTime.MIN);
            calendarEntry.setEntryTo(LocalDateTime.MAX);
            courseDto = new CourseDto();
            courseDto.setNewCalendarEntry(calendarEntry);
        }

        @Test
        void setCalendarEntry() {
            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
            assertThat(actualCourse.getCalendarEntries())
                    .singleElement()
                    .isEqualTo(calendarEntry);
            assertEquals(calendarEntry.getEntryFrom(), actualCourse.getMinTime());
            assertEquals(calendarEntry.getEntryTo(), actualCourse.getMaxTime());
        }

        @Test
        void setRecurrenceOption() {
            courseDto.setNewCalendarEntry(null);
            var daily = new DailyRecurrenceOption();
            daily.setStartTime(LocalTime.of(1, 0, 0));
            daily.setEndTime(LocalTime.of(3, 0, 0));
            daily.setStartRecurrence(LocalDate.of(2024, 10, 1));
            daily.setEndRecurrence(LocalDate.of(2024, 10, 3));
            courseDto.setRecurrenceOption(daily);
            var minTime = LocalDateTime.of(daily.getStartRecurrence(), daily.getStartTime());
            var maxTime = LocalDateTime.of(daily.getEndRecurrence(), daily.getEndTime());

            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
            assertThat(actualCourse.getCalendarEntries())
                    .hasSize(3);
            assertEquals(minTime, actualCourse.getMinTime());
            assertEquals(maxTime, actualCourse.getMaxTime());
        }

        @Test
        void setNewMember() {
            var memberReference = mockMember();
            courseDto.setNewMemberId(memberReference.getId());

            courseService.save(courseDto);

            assertEquals(memberReference, getCourseSaved().getMember());
        }

        @Test
        void keepSameMember() {
            var memberReference = new MemberReference();
            memberReference.setId(UUID.randomUUID());
            courseDto.setMember(memberReference);

            courseService.save(courseDto);

            verifyNoInteractions(memberReferenceRepository);
            assertEquals(memberReference, getCourseSaved().getMember());
        }

        @Test
        void updateMember() {
            var actualMember = new MemberReference();
            actualMember.setId(UUID.randomUUID());
            var newMember = mockMember();
            courseDto.setMember(actualMember);
            courseDto.setNewMemberId(newMember.getId());

            courseService.save(courseDto);

            assertEquals(newMember, getCourseSaved().getMember());
        }

        @Test
        void addCoTrainer() {
            var memberReference = mockMember();
            var coTrainer = new CoTrainer(memberReference);
            courseDto.getNewCoTrainers().add(coTrainer.getId());

            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
            assertThat(actualCourse.getCoTrainers())
                    .singleElement()
                    .isEqualTo(coTrainer);
        }

        @Test
        void addSpace() {
            var dogHasHandlerReference = new DogHasHandlerReference();
            dogHasHandlerReference.setId(UUID.randomUUID());
            dogHasHandlerReference.setDog(new DogReference());
            dogHasHandlerReference.setMember(new MemberReference());
            var space = new Space(dogHasHandlerReference);
            courseDto.setNewSpaces(Set.of(space.getId()));
            when(dogHasHandlerReferenceRepository.findByIdAndEntityStatus(space.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandlerReference));

            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
            assertThat(actualCourse.getSpaces())
                    .singleElement()
                    .isEqualTo(space);
        }

        private Course getCourseSaved() {
            verify(courseRepository).setMetadataAndSave(courseCaptor.capture());
            return courseCaptor.getValue();
        }

        private MemberReference mockMember() {
            var memberReference = new MemberReference();
            memberReference.setId(UUID.randomUUID());
            when(memberReferenceRepository.findOneByIdAndEntityStatus(memberReference.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(memberReference));
            return memberReference;
        }
    }

    @Nested
    class GetOneById {
        @Test
        void exist() {
            when(courseRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            var courseDto = courseService.getOneById(course.getId());

            assertEquals(course.getId(), courseDto.getId());
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(courseRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.getOneById(id));
        }
    }
}
