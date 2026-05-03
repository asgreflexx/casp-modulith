package casp.web.backend.calendar;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.CourseRepository;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReference;
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
import org.springframework.dao.OptimisticLockingFailureException;
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
import static casp.web.backend.calendar.CourseMapper.COURSE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Captor
    private ArgumentCaptor<Course> courseCaptor;

    private Course course;

    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setName("course");
        courseService = new CourseServiceImpl(courseRepository);
        courseService.setZoneId(ZONE_ID);
        courseService.setMemberReferenceRepository(memberReferenceRepository);
        courseService.setDogHasHandlerReferenceRepository(dogHasHandlerReferenceRepository);
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
    void getCoursesFeesStats() {
        var expectedCoursesFeesStatsDto = mock(CoursesFeesStatsDto.class);
        when(courseRepository.getCoursesFeesStats()).thenReturn(expectedCoursesFeesStatsDto);

        var actualCoursesFeesStatsDto = courseService.getCoursesFeesStats();

        assertEquals(expectedCoursesFeesStatsDto, actualCoursesFeesStatsDto);
    }

    @Nested
    class UpdateSpaces {

        private Space space;

        @BeforeEach
        void setUp() {
            space = createSpace();
            space.setNote("spaceChanged");
            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));
        }

        @Test
        void savesSuccessfully() {
            courseService.updateSpaces(course.getId(), course.getVersion(), Set.of(COURSE_MAPPER.toSpaceDto(space)));

            var courseDto = getCourseSaved();

            assertThat(courseDto.getParticipants())
                    .singleElement()
                    .satisfies(s -> assertEquals(space.getNote(), s.getNote()));
        }

        @Test
        void throwsOptimisticLockingFailure() {
            var id = course.getId();
            var badVersion = course.getVersion() + 1;
            var spaceDtos = Set.of(COURSE_MAPPER.toSpaceDto(space));

            assertThrows(OptimisticLockingFailureException.class, () -> courseService.updateSpaces(id, badVersion, spaceDtos));
            verify(courseRepository, never()).save(course);
        }
    }

    @Nested
    class GetEmailsByCourseId {
        @Test
        void exist() {
            var member = ReferenceTestFixture.createMemberReference();
            var dogHasHandler = new DogHasHandlerReference();
            dogHasHandler.setMember(member);
            dogHasHandler.setDog(new DogReference());
            var space = new Space(dogHasHandler);
            course.setParticipants(Set.of(space));
            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            var emailSet = courseService.getEmailsByCourseId(course.getId());

            assertThat(emailSet)
                    .singleElement()
                    .isEqualTo(member.getEmail());
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(courseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.getEmailsByCourseId(id));
        }
    }

    @Nested
    class DeleteById {
        @Test
        void exist() {
            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            courseService.deleteById(course.getId());

            verify(courseRepository).save(courseCaptor.capture());
            assertThat(courseCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.DELETED);
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(courseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.deleteById(id));
        }
    }

    @Nested
    class Save {
        private CourseDto courseDto;
        private NewCalendarEntryDto newCalendarEntryDto;

        @BeforeEach
        void setUp() {
            newCalendarEntryDto = createNewCalendarEntryDto();
            courseDto = new CourseDto();
            courseDto.setNewCalendarEntry(newCalendarEntryDto);
        }

        @Test
        void setCalendarEntry() {
            var memberReference = mockMember();
            courseDto.setMemberId(memberReference.getId());

            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
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
            courseDto.setMemberId(memberReference.getId());
            courseDto.setNewCalendarEntry(null);
            var daily = new DailyRecurrenceOption();
            daily.setStartTime(LocalTime.of(1, 0, 0));
            daily.setEndTime(LocalTime.of(3, 0, 0));
            daily.setStartRecurrence(createLocalDate(0));
            daily.setEndRecurrence(createLocalDate(2));
            courseDto.setRecurrenceOption(daily);

            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
            assertThat(actualCourse.getCalendarEntries())
                    .hasSize(3);
        }

        @Test
        void setNewMember() {
            var memberReference = mockMember();
            courseDto.setMemberId(memberReference.getId());

            courseService.save(courseDto);

            assertEquals(memberReference, getCourseSaved().getMember());
        }

        @Test
        void memberDoesNotExist() {
            var newMemberId = UUID.randomUUID();
            courseDto.setMemberId(newMemberId);
            when(memberReferenceRepository.findOneByIdAndEntityStatus(newMemberId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.save(courseDto));
        }

        @Test
        void addCoTrainer() {
            var memberReference = mockMember();
            var coTrainer = new CoTrainer(memberReference);
            courseDto.setMemberId(memberReference.getId());
            courseDto.getCoTrainerIds().add(coTrainer.getId());

            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
            assertThat(actualCourse.getCoTrainers())
                    .singleElement()
                    .isEqualTo(coTrainer);
        }

        @Test
        void addSpace() {
            var memberReference = mockMember();
            courseDto.setMemberId(memberReference.getId());
            var dogHasHandlerReference = new DogHasHandlerReference();
            dogHasHandlerReference.setId(UUID.randomUUID());
            dogHasHandlerReference.setDog(new DogReference());
            dogHasHandlerReference.setMember(ReferenceTestFixture.createMemberReference());
            var space = new Space(dogHasHandlerReference);
            courseDto.setParticipantIds(Set.of(space.getId()));
            when(dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(space.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandlerReference));

            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
            assertThat(actualCourse.getParticipants())
                    .singleElement()
                    .isEqualTo(space);
        }

        private MemberReference mockMember() {
            var memberReference = ReferenceTestFixture.createMemberReference();
            when(memberReferenceRepository.findOneByIdAndEntityStatus(memberReference.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(memberReference));
            return memberReference;
        }
    }

    @Nested
    class GetOneById {
        @Test
        void exist() {
            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            var courseDto = courseService.getOneById(course.getId());

            assertEquals(course.getId(), courseDto.getId());
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(courseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.getOneById(id));
        }
    }

    @Test
    void getCoursesByDogHasHandlerId() {
        var dogHasHandlerId = UUID.randomUUID();
        when(courseRepository.findAllBySpaceId(dogHasHandlerId, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(course)));

        var courseDtoPage = courseService.getCoursesByDogHasHandlerId(dogHasHandlerId, Pageable.unpaged());

        assertThat(courseDtoPage)
                .containsExactly(COURSE_MAPPER.toTarget(course));
    }

    private static Space createSpace() {
        var member = ReferenceTestFixture.createMemberReference();
        var dogHasHandler = new DogHasHandlerReference();
        dogHasHandler.setMember(member);
        dogHasHandler.setDog(new DogReference());
        return new Space(dogHasHandler);
    }

    private Course getCourseSaved() {
        verify(courseRepository).save(courseCaptor.capture());
        return courseCaptor.getValue();
    }
}
