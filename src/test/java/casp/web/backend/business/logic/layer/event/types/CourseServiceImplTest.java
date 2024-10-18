package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.common.enums.BaseEventOptionType;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.types.CourseRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.Calendar;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.CoTrainer;
import casp.web.backend.deprecated.event.participants.Space;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CalendarService calendarService;
    @Mock
    private SpaceService participantService;
    @Mock
    private CoTrainerService coTrainerService;
    @Mock
    private BaseEventRepository eventRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CalendarRepository calendarRepository;
    @Mock
    private BaseParticipantRepository participantRepository;
    @Mock
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private CourseRepository courseV2Repository;
    @Captor
    private ArgumentCaptor<casp.web.backend.data.access.layer.event.types.Course> courseV2Captor;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;

    @BeforeEach
    void setUp() {
        course = spy(TestFixture.createCourse());
    }

    @Test
    void saveBaseEvent() {
        var member = course.getMember();
        course.setMember(null);
        when(eventRepository.save(course)).thenAnswer(invocation -> invocation.getArgument(0));
        when(memberRepository.findByIdAndEntityStatus(course.getMemberId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

        verify(course).setMember(member);
    }

    @Test
    void deleteById() {
        when(eventRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

        courseService.deleteById(course.getId());

        verify(participantService).deleteParticipantsByBaseEventId(course.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(course.getId());
        verify(coTrainerService).deleteParticipantsByBaseEventId(course.getId());
        assertSame(EntityStatus.DELETED, course.getEntityStatus());
    }

    @Test
    void getBaseEventsAsPage() {
        var page = new PageImpl<BaseEvent>(List.of(course));
        var year = LocalDate.now().getYear();
        when(eventRepository.findAllByYearAndEventType(year, course.getEventType(), Pageable.unpaged())).thenReturn(page);

        assertThat(courseService.getAllByYear(year, Pageable.unpaged()).getContent()).
                singleElement()
                .isEqualTo(course);
    }

    @Test
    void deleteBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, course.getEventType())).thenReturn(Set.of(course));

        courseService.deleteBaseEventsByMemberId(memberId);

        verify(participantService).deleteParticipantsByBaseEventId(course.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(course.getId());
        verify(coTrainerService).deleteParticipantsByBaseEventId(course.getId());
        assertSame(EntityStatus.DELETED, course.getEntityStatus());
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, course.getEventType())).thenReturn(Set.of(course));

        courseService.deactivateBaseEventsByMemberId(memberId);

        verify(participantService).deactivateParticipantsByBaseEventId(course.getId());
        verify(calendarService).deactivateCalendarEntriesByBaseEventId(course.getId());
        verify(coTrainerService).deactivateParticipantsByBaseEventId(course.getId());
        assertSame(EntityStatus.INACTIVE, course.getEntityStatus());
    }

    @Test
    void activateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, course.getEventType())).thenReturn(Set.of(course));

        courseService.activateBaseEventsByMemberId(memberId);

        verify(participantService).activateParticipantsByBaseEventId(course.getId());
        verify(calendarService).activateCalendarEntriesByBaseEventId(course.getId());
        verify(coTrainerService).activateParticipantsByBaseEventId(course.getId());
        assertSame(EntityStatus.ACTIVE, course.getEntityStatus());
    }

    @Nested
    class MigrateDataToV2 {
        private static final Sort SORT = Sort.by("eventFrom").ascending().and(Sort.by("eventTo").ascending());
        @Mock
        private MemberReference courseMember;

        private casp.web.backend.data.access.layer.event.types.Course courseV2;
        private Calendar calendar;


        @BeforeEach
        void setUp() {
            when(eventRepository.findAllByEventType(Course.EVENT_TYPE)).thenReturn(Set.of(course));
        }

        @Test
        void deleteAll() {
            courseService.migrateDataToV2();

            verify(courseV2Repository).deleteAll();
        }

        @Test
        void courseMemberNotFound() {
            when(memberReferenceRepository.findById(course.getMemberId())).thenReturn(Optional.empty());

            courseService.migrateDataToV2();

            verifyNoInteractions(calendarRepository, participantRepository, dogHasHandlerReferenceRepository);
            verify(courseV2Repository).deleteAll();
            verify(courseV2Repository, times(0)).save(any(casp.web.backend.data.access.layer.event.types.Course.class));
        }

        @Test
        void spaces() {
            findCourseMemberAndCalendar();
            var spaceDogHasHandler = mock(DogHasHandlerReference.class, Answers.RETURNS_DEEP_STUBS);
            when(spaceDogHasHandler.isActive()).thenReturn(true);
            var space = TestFixture.createSpace();
            when(participantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), Space.PARTICIPANT_TYPE)).thenReturn(Set.of(space));
            when(participantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of());
            when(dogHasHandlerReferenceRepository.findById(space.getMemberOrHandlerId())).thenReturn(Optional.of(spaceDogHasHandler));

            courseService.migrateDataToV2();

            assertCourseV2();
            assertThat(courseV2.getSpaces()).singleElement().satisfies(s -> assertSame(spaceDogHasHandler, s.getDogHasHandler()));
        }

        @Test
        void dogHasHandlerForSpaceNotFound() {
            findCourseMemberAndCalendar();
            var space = TestFixture.createSpace();
            when(participantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), Space.PARTICIPANT_TYPE)).thenReturn(Set.of(space));
            when(participantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of());
            when(dogHasHandlerReferenceRepository.findById(space.getMemberOrHandlerId())).thenReturn(Optional.empty());

            courseService.migrateDataToV2();

            assertCourseV2();
            assertThat(courseV2.getSpaces()).isEmpty();
        }

        @Test
        void coTrainers() {
            findCourseMemberAndCalendar();
            var coTrainerMember = mock(MemberReference.class, Answers.RETURNS_DEEP_STUBS);
            when(coTrainerMember.getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
            var coTrainer = TestFixture.createCoTrainer();
            when(participantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), Space.PARTICIPANT_TYPE)).thenReturn(Set.of());
            when(participantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of(coTrainer));
            when(memberReferenceRepository.findById(coTrainer.getMemberOrHandlerId())).thenReturn(Optional.of(coTrainerMember));

            courseService.migrateDataToV2();

            assertCourseV2();
            assertThat(courseV2.getCoTrainers()).singleElement().satisfies(ct -> assertSame(coTrainerMember, ct.getMember()));
        }

        @Test
        void memberForCoTrainerNotFound() {
            findCourseMemberAndCalendar();
            var coTrainer = TestFixture.createCoTrainer();
            when(participantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), Space.PARTICIPANT_TYPE)).thenReturn(Set.of());
            when(participantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of(coTrainer));
            when(memberReferenceRepository.findById(coTrainer.getMemberOrHandlerId())).thenReturn(Optional.empty());

            courseService.migrateDataToV2();

            assertCourseV2();
            assertThat(courseV2.getCoTrainers()).isEmpty();
        }

        @Test
        void daily() {
            findCourseMemberAndCalendar();
            course.setDailyOption(TestFixture.createDailyEventOption());

            courseService.migrateDataToV2();

            assertCourseV2();
            assertSame(BaseEventOptionType.DAILY, courseV2.getBaseEventOption().getOptionType());
        }

        @Test
        void weekly() {
            findCourseMemberAndCalendar();
            course.setWeeklyOption(TestFixture.createWeeklyEventOption());

            courseService.migrateDataToV2();

            assertCourseV2();
            assertSame(BaseEventOptionType.WEEKLY, courseV2.getBaseEventOption().getOptionType());
        }

        private void findCourseMemberAndCalendar() {
            when(memberReferenceRepository.findById(course.getMemberId())).thenReturn(Optional.of(courseMember));
            calendar = TestFixture.createCalendarEntry();
            when(calendarRepository.findAllByBaseEventId(course.getId(), SORT)).thenReturn(List.of(calendar));
        }

        private void assertCourseV2() {
            verify(courseV2Repository).save(courseV2Captor.capture());
            courseV2 = courseV2Captor.getValue();
            assertEquals(course.getId(), courseV2.getId());
            assertSame(courseMember, courseV2.getMember());
            assertThat(courseV2.getCalendarEntries()).singleElement().satisfies(ce -> assertEquals(calendar.getId(), ce.getId()));
        }
    }

    @Nested
    class GetBaseEventById {
        @BeforeEach
        void setUp() {
            course.setMember(null);
        }

        @Test
        void eventExist() {
            when(eventRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            assertSame(course, courseService.getOneById(course.getId()));
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.getOneById(id));
        }

        @Test
        void memberExist() {
            var member = TestFixture.createMember();
            course.setMemberId(member.getId());
            when(eventRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));
            when(memberRepository.findByIdAndEntityStatus(course.getMemberId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            var actualCourse = courseService.getOneById(course.getId());

            assertSame(course.getMemberId(), actualCourse.getMember().getId());
        }

        @Test
        void memberDoesNotExist() {
            when(eventRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));
            when(memberRepository.findByIdAndEntityStatus(course.getMemberId(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            var actualCourse = courseService.getOneById(course.getId());

            assertNull(actualCourse.getMember());
        }

        @Test
        void memberWasAlreadySet() {
            var member = TestFixture.createMember();
            course.setMemberId(member.getId());
            course.setMember(member);
            when(eventRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));

            courseService.getOneById(course.getId());

            verifyNoInteractions(memberRepository);
        }
    }
}
