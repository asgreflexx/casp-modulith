package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.member.MemberRepository;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
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

        assertSame(course, courseService.save(course));
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
