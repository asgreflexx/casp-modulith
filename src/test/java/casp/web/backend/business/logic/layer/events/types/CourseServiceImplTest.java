package casp.web.backend.business.logic.layer.events.types;


import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.mappers.CourseMapperImpl;
import casp.web.backend.business.logic.layer.events.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.events.participants.SpaceService;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.CoTrainer;
import casp.web.backend.data.access.layer.documents.event.participant.Space;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Course;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private Calendar calendarEntry;
    private List<Calendar> calendarEntries;
    private Set<Space> participants;
    private Set<CoTrainer> coTrainers;

    @BeforeEach
    void setUp() {
        course = TestFixture.createValidCourse();
        calendarEntry = TestFixture.createValidCalendarEntry(course);
        calendarEntries = List.of(calendarEntry);
        participants = Set.of(TestFixture.createValidSpace(course));
        coTrainers = Set.of(TestFixture.createValidCoTrainer(course));
    }

    @Test
    void saveBaseEventDto() {
        var courseMapper = new CourseMapperImpl();
        var courseDto = courseMapper.documentToDto(course);
        courseDto.setCalendarEntries(calendarEntries);
        courseDto.setParticipants(participants);
        courseDto.setCoTrainers(coTrainers);
        when(calendarService.replaceCalendarEntriesFromEvent(course, calendarEntries.getFirst())).thenReturn(calendarEntries);
        when(participantService.saveParticipants(courseDto.getParticipants())).thenReturn(courseDto.getParticipants());
        when(coTrainerService.saveParticipants(courseDto.getCoTrainers())).thenReturn(courseDto.getCoTrainers());
        when(eventRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var actualCourse = courseService.saveBaseEventDto(courseDto);

        assertSame(calendarEntry.getEventTo(), actualCourse.getMaxLocalDateTime());
        assertSame(calendarEntry.getEventFrom(), actualCourse.getMinLocalDateTime());
        assertEquals(calendarEntries, actualCourse.getCalendarEntries());
        assertEquals(participants.size(), actualCourse.getParticipantsSize());
        assertEquals(participants, actualCourse.getParticipants());
        assertEquals(coTrainers, actualCourse.getCoTrainers());
        assertEquals(coTrainers.size(), actualCourse.getCoTrainers().size());
    }

    @Test
    void deleteBaseEventById() {
        when(eventRepository.findByIdAndEntityStatusNot(course.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(course));

        courseService.deleteBaseEventById(course.getId());

        assertSame(EntityStatus.DELETED, course.getEntityStatus());
    }

    @Test
    void createNewBaseEventWithOneCalendarEntry() {
        var courseDto = courseService.createNewBaseEventWithOneCalendarEntry();

        assertThat(courseDto.getCalendarEntries())
                .singleElement()
                .satisfies(calendar -> {
                    // It is hard to compare the LocalDateTime objects directly, because of the time zone.
                    assertThat(calendar.getEventFrom()).isNotNull();
                    assertThat(calendar.getEventTo()).isNotNull();
                });
    }

    @Test
    void getBaseEventsAsPage() {
        var page = new PageImpl<BaseEvent>(List.of(course));
        var year = LocalDate.now().getYear();
        when(eventRepository.findAllByYearAndEventType(year, course.getEventType(), Pageable.unpaged())).thenReturn(page);

        assertThat(courseService.getBaseEventsAsPage(year, Pageable.unpaged()).getContent()).
                singleElement()
                .isEqualTo(course);
    }

    @Test
    void deleteBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusNot(memberId, EntityStatus.DELETED)).thenReturn(Set.of(course));

        courseService.deleteBaseEventsByMemberId(memberId);

        assertSame(EntityStatus.DELETED, course.getEntityStatus());
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Set.of(course));

        courseService.deactivateBaseEventsByMemberId(memberId);

        assertSame(EntityStatus.INACTIVE, course.getEntityStatus());
    }

    @Test
    void activateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE)).thenReturn(Set.of(course));

        courseService.activateBaseEventsByMemberId(memberId);

        assertSame(EntityStatus.ACTIVE, course.getEntityStatus());
    }

    @Nested
    class GetBaseEventDtoById {
        @Test
        void eventExist() {
            when(eventRepository.findByIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(course));
            when(calendarService.getCalendarEntriesByBaseEvent(course)).thenReturn(calendarEntries);
            when(participantService.getParticipantsByEvent(course)).thenReturn(participants);
            when(coTrainerService.getParticipantsByEvent(course)).thenReturn(coTrainers);

            var examDto = courseService.getBaseEventDtoById(course.getId());

            assertEquals(calendarEntries, examDto.getCalendarEntries());
            assertSame(participants, examDto.getParticipants());
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> courseService.getBaseEventDtoById(id));
        }
    }
}
