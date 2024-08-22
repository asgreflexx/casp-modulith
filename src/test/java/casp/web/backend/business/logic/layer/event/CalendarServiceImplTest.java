package casp.web.backend.business.logic.layer.event;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import casp.web.backend.data.access.layer.repositories.CalendarRepository;
import casp.web.backend.presentation.layer.dtos.events.CourseDto;
import casp.web.backend.presentation.layer.dtos.events.EventDto;
import casp.web.backend.presentation.layer.dtos.events.ExamDto;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {
    private final ArgumentCaptor<Predicate> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);

    @Mock
    private CalendarRepository calendarRepository;
    @Mock
    private BaseEventRepository baseEventRepository;
    @Mock
    private BaseParticipantRepository baseParticipantRepository;

    @Mock
    private CourseService courseService;
    @Mock
    private ExamService examService;
    @Mock
    private EventService eventService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Calendar calendar;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    @Nested
    class SetMemberEntriesAndEventsStatus {
        private static final String QUERY = "baseParticipant.entityStatus != DELETED && (baseParticipant.memberOrHandlerId = %s || baseParticipant.baseEvent in [])";

        private UUID memberId;

        @BeforeEach
        void setUp() {
            memberId = UUID.randomUUID();
        }

        @Test
        void eventsIsEmpty() {
            var eventParticipant = spy(new EventParticipant());
            when(baseEventRepository.findAllByMemberIdAndEntityStatusNotLike(memberId, EntityStatus.DELETED)).thenReturn(Collections.emptySet());
            when(baseParticipantRepository.findAll(any(Predicate.class))).thenReturn(Collections.singleton(eventParticipant));

            calendarService.setMemberEntriesAndEventsStatus(memberId, EntityStatus.INACTIVE);

            verifyNoInteractions(calendarRepository);
            verify(baseParticipantRepository).findAll(predicateArgumentCaptor.capture());
            assertEquals(QUERY.formatted(memberId), predicateArgumentCaptor.getValue().toString());
            verify(eventParticipant).setEntityStatus(EntityStatus.INACTIVE);
        }

        @Test
        void eventsIsNotEmpty() {
            BaseEvent event = spy(new Event());
            var events = Set.of(event);
            var eventCalendarEntry = spy(new Calendar());
            when(baseEventRepository.findAllByMemberIdAndEntityStatusNotLike(memberId, EntityStatus.DELETED)).thenReturn(events);
            when(baseParticipantRepository.findAll(any(Predicate.class))).thenReturn(Collections.emptySet());
            when(calendarRepository.findAllByBaseEventInAndEntityStatusNotLike(events, EntityStatus.DELETED)).thenReturn(Set.of(eventCalendarEntry));

            calendarService.setMemberEntriesAndEventsStatus(memberId, EntityStatus.INACTIVE);

            verify(calendarRepository).findAllByBaseEventInAndEntityStatusNotLike(events, EntityStatus.DELETED);
            verify(baseParticipantRepository).findAll(predicateArgumentCaptor.capture());
            assertThat(predicateArgumentCaptor.getValue().toString()).contains(memberId.toString(), Event.EVENT_TYPE);
            verify(event).setEntityStatus(EntityStatus.INACTIVE);
            verify(eventCalendarEntry).setEntityStatus(EntityStatus.INACTIVE);
        }
    }

    @Nested
    class GetCalendarEntriesByPeriodAndEventTypes {
        public static final String QUERY = "calendar.entityStatus = ACTIVE && calendar.eventFrom >= -999999999-01-01T00:00 && calendar.eventTo <= +999999999-12-31T23:59:59.999999999";

        @BeforeEach
        void setUp() {
            when(calendarRepository.findAll(any(Predicate.class))).thenReturn(Set.of(calendar));
        }

        @Test
        void withoutEventTypes() {
            var entries = calendarService.getCalendarEntriesByPeriodAndEventTypes(LocalDate.MIN, LocalDate.MAX, null);

            verify(calendarRepository).findAll(predicateArgumentCaptor.capture());
            assertEquals(QUERY, predicateArgumentCaptor.getValue().toString());
            assertThat(entries).containsExactly(calendar);
        }

        @Test
        void withEventTypes() {
            var entries = calendarService.getCalendarEntriesByPeriodAndEventTypes(LocalDate.MIN, LocalDate.MAX, Set.of(Exam.EVENT_TYPE, Course.EVENT_TYPE));

            verify(calendarRepository).findAll(predicateArgumentCaptor.capture());
            assertThat(predicateArgumentCaptor.getValue().toString()).contains(QUERY, Exam.EVENT_TYPE, Course.EVENT_TYPE);
            assertThat(entries).containsExactly(calendar);
        }
    }

    @Nested
    class GetBaseEventEntryById {
        @ParameterizedTest
        @ValueSource(strings = {Exam.EVENT_TYPE, Event.EVENT_TYPE, Course.EVENT_TYPE})
        void getBaseEventEntryById(String eventType) {
            var calendarId = UUID.randomUUID();
            when(calendarRepository.findCalendarByEntityStatusAndId(EntityStatus.ACTIVE, calendarId)).thenReturn(Optional.of(calendar));
            when(calendar.getBaseEvent().getEventType()).thenReturn(eventType);
            when(calendar.getBaseEvent().getId()).thenReturn(UUID.randomUUID());
            mockService(eventType);

            var baseEventDto = calendarService.getBaseEventEntryById(calendarId);

            assertEquals(eventType, baseEventDto.getEventType());
            assertThat(baseEventDto.getEntries()).containsExactly(calendar);
        }

        @Test
        void calendarNotFound() {
            var calendarId = UUID.randomUUID();
            when(calendarRepository.findCalendarByEntityStatusAndId(EntityStatus.ACTIVE, calendarId)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> calendarService.getBaseEventEntryById(calendarId));
        }

        private void mockService(final String eventType) {
            switch (eventType) {
                case Exam.EVENT_TYPE ->
                        when(examService.getActiveEventDtoById(calendar.getBaseEvent().getId())).thenReturn(new ExamDto());
                case Course.EVENT_TYPE ->
                        when(courseService.getActiveEventDtoById(calendar.getBaseEvent().getId())).thenReturn(new CourseDto());
                default ->
                        when(eventService.getActiveEventDtoById(calendar.getBaseEvent().getId())).thenReturn(new EventDto());
            }
        }
    }
}
