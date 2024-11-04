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

    private static Space createSpace() {
        var member = new MemberReference();
        member.setEmail("mail@mail");
        var dogHasHandler = new DogHasHandlerReference();
        dogHasHandler.setMember(member);
        dogHasHandler.setDog(new DogReference());
        return new Space(dogHasHandler);
    }

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

    @Test
    void getCalendarEntries() {
        var from = LocalDateTime.now().minusDays(1);
        var to = from.plusDays(1);
        var calendarEntry1 = new CalendarEntry(from.minusHours(1), from);
        var calendarEntry2 = new CalendarEntry(from, to);
        var calendarEntry3 = new CalendarEntry(to, to.plusHours(1));
        course.setCalendarEntries(new ArrayList<>(List.of(calendarEntry1, calendarEntry2, calendarEntry3)));
        when(courseRepository.findAllBetweenFromAndTo(from, to)).thenReturn(Stream.of(course));

        var calendarEntryDtoStream = courseService.getCalendarEntriesBetweenFromAndTo(from, to);

        assertThat(calendarEntryDtoStream)
                .singleElement()
                .satisfies(ce -> {
                    assertEquals(calendarEntry2.getEntryFrom(), ce.getEntryFrom());
                    assertEquals(calendarEntry2.getEntryTo(), ce.getEntryTo());
                    assertSame(course.getEventType(), ce.getEventType());
                });
    }

    @Nested
    class GetOneByIdAndCalendarEntryId {

        private CalendarEntry calendarEntry;

        @BeforeEach
        void setUp() {
            calendarEntry = new CalendarEntry(LocalDateTime.MIN, LocalDateTime.MAX);
            course.addCalendarEntry(calendarEntry);
            course.addCalendarEntry(new CalendarEntry(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        }

        @Test
        void courseExist() {
            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            var courseDto = courseService.getOneByIdAndCalendarEntryId(course.getId(), calendarEntry.getId());

            assertEquals(course.getId(), courseDto.getId());
            assertThat(courseDto.getCalendarEntries())
                    .singleElement()
                    .satisfies(ce -> {
                        assertEquals(calendarEntry.getEntryFrom(), ce.getEntryFrom());
                        assertEquals(calendarEntry.getEntryTo(), ce.getEntryTo());
                    });
        }

        @Test
        void courseDoesNotExist() {
            var id = UUID.randomUUID();
            when(courseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.getOneByIdAndCalendarEntryId(id, calendarEntry.getId()));
        }

        @Test
        void calendarEntryDoesNotExist() {
            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            assertThrows(NoSuchElementException.class, () -> courseService.getOneByIdAndCalendarEntryId(course.getId(), UUID.randomUUID()));
        }
    }

    @Nested
    class RemoveSpace {
        @Test
        void spaceExist() {
            var space = createSpace();
            course.addSpace(space);

            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            courseService.removeSpace(course.getId(), space.getId());

            verify(courseRepository).save(courseCaptor.capture());

            assertThat(courseCaptor.getValue().getSpaces())
                    .isEmpty();
        }

        @Test
        void courseDoesNotExist() {
            var id = UUID.randomUUID();
            var idSpace = UUID.randomUUID();
            when(courseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.removeSpace(id, idSpace));
        }

        @Test
        void spaceDoesNotExist() {
            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            assertThrows(NoSuchElementException.class, () -> courseService.removeSpace(course.getId(), UUID.randomUUID()));
        }
    }

    @Nested
    class SaveSpace {
        @Test
        void updateSpace() {
            var space = createSpace();
            course.addSpace(space);

            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

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
            when(courseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.saveSpace(id, space));
        }

        @Test
        void spaceDoesNotExist() {
            when(courseRepository.findOneByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            assertThrows(NoSuchElementException.class, () -> courseService.saveSpace(course.getId(), createSpace()));
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
            newCalendarEntryDto = new NewCalendarEntryDto();
            newCalendarEntryDto.setEntryFrom(LocalDateTime.MIN);
            newCalendarEntryDto.setEntryTo(LocalDateTime.MAX);
            courseDto = new CourseDto();
            courseDto.setNewCalendarEntry(newCalendarEntryDto);
        }

        @Test
        void setCalendarEntry() {
            courseService.save(courseDto);

            var actualCourse = getCourseSaved();
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
        void memberDoesNotExist() {
            var newMemberId = UUID.randomUUID();
            courseDto.setNewMemberId(newMemberId);
            when(memberReferenceRepository.findOneByIdAndEntityStatus(newMemberId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.save(courseDto));
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
            when(dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(space.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandlerReference));

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
}
