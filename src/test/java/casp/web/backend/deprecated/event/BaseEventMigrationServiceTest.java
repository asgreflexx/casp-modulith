package casp.web.backend.deprecated.event;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.calendar.options.BaseRecurrenceOptionType;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.calendar.Calendar;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.options.DailyEventOption;
import casp.web.backend.deprecated.event.options.WeeklyEventOption;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.CoTrainer;
import casp.web.backend.deprecated.event.participants.EventParticipant;
import casp.web.backend.deprecated.event.participants.ExamParticipant;
import casp.web.backend.deprecated.event.participants.Space;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Course;
import casp.web.backend.deprecated.event.types.Event;
import casp.web.backend.deprecated.event.types.Exam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BaseEventMigrationServiceTest {
    private static final Sort SORT = Sort.by("eventFrom").ascending().and(Sort.by("eventTo").ascending());
    private static final String LOCATION = "location";

    @Mock
    private BaseEventRepository baseEventRepository;
    @Mock
    private BaseParticipantRepository baseParticipantRepository;
    @Mock
    private CalendarRepository calendarRepository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Mock
    private Calendar calendar;
    @Mock
    private MemberReference memberReference;
    @InjectMocks
    private BaseEventMigrationService baseEventMigrationService;
    private UUID memberId;
    private UUID id;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        id = UUID.randomUUID();
        when(calendar.getEventFrom()).thenReturn(LocalDateTime.MIN);
        when(calendar.getEventTo()).thenReturn(LocalDateTime.MAX);
        when(calendar.getLocation()).thenReturn(LOCATION);
        when(memberReference.getId()).thenReturn(memberId);
        when(calendarRepository.findAllByBaseEventId(id, SORT)).thenReturn(List.of(calendar));
        when(memberReferenceRepository.findById(memberId)).thenReturn(Optional.of(memberReference));
    }

    private void assertV2(BaseEvent<?> baseEvent) {
        assertEquals(id, baseEvent.getId());
        assertEquals(memberId, baseEvent.getMember().getId());
        assertEquals(LOCATION, baseEvent.getLocation());
        assertThat(baseEvent.getCalendarEntries())
                .singleElement()
                .satisfies(ce -> {
                    assertEquals(LocalDateTime.MIN, ce.getEntryFrom());
                    assertEquals(LocalDateTime.MAX, ce.getEntryTo());
                });
        assertEquals(LocalDateTime.MIN, baseEvent.getMinTime());
        assertEquals(LocalDateTime.MAX, baseEvent.getMaxTime());
    }

    @Nested
    class MapToEventV2 {
        @Mock
        private Event event;

        @BeforeEach
        void setUp() {
            when(event.getMemberId()).thenReturn(memberId);
            when(event.getId()).thenReturn(id);
            when(baseEventRepository.findAllByEventType(Event.EVENT_TYPE)).thenReturn(Set.of(event));
        }

        @Test
        void participant() {
            var participant = mock(EventParticipant.class, Answers.RETURNS_DEEP_STUBS);
            var participantMember = mock(MemberReference.class);
            var participantMemberId = UUID.randomUUID();
            when(participant.getMemberOrHandlerId()).thenReturn(participantMemberId);
            when(participantMember.getId()).thenReturn(participantMemberId);
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, EventParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of(participant));
            when(memberReferenceRepository.findById(participantMemberId)).thenReturn(Optional.of(participantMember));

            var eventV2Set = baseEventMigrationService.mapToEventV2();

            assertThat(eventV2Set)
                    .singleElement()
                    .satisfies(eventV2 -> {
                        assertV2(eventV2);
                        assertThat(eventV2.getParticipants())
                                .singleElement()
                                .satisfies(p -> assertEquals(participantMemberId, p.getId()));
                    });
        }

        @Test
        void daily() {
            when(event.getDailyOption()).thenReturn(mock(DailyEventOption.class, Answers.RETURNS_DEEP_STUBS));
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, EventParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of());

            var eventV2Set = baseEventMigrationService.mapToEventV2();

            assertThat(eventV2Set)
                    .singleElement()
                    .satisfies(eventV2 -> {
                        assertV2(eventV2);
                        assertEquals(BaseRecurrenceOptionType.DAILY, eventV2.getRecurrenceOption().getOptionType());
                    });
        }

        @Test
        void weekly() {
            when(event.getWeeklyOption()).thenReturn(mock(WeeklyEventOption.class, Answers.RETURNS_DEEP_STUBS));
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, EventParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of());

            var eventV2Set = baseEventMigrationService.mapToEventV2();

            assertThat(eventV2Set)
                    .singleElement()
                    .satisfies(eventV2 -> {
                        assertV2(eventV2);
                        assertEquals(BaseRecurrenceOptionType.WEEKLY, eventV2.getRecurrenceOption().getOptionType());
                    });
        }

    }

    @Nested
    class MapToExamV2 {
        @Mock
        private Exam exam;

        @BeforeEach
        void setUp() {
            when(exam.getMemberId()).thenReturn(memberId);
            when(exam.getId()).thenReturn(id);
            when(baseEventRepository.findAllByEventType(Exam.EVENT_TYPE)).thenReturn(Set.of(exam));
        }

        @Test
        void mapSpaces() {
            var participant = mock(ExamParticipant.class);
            var dhh = mock(DogHasHandlerReference.class, Answers.RETURNS_DEEP_STUBS);
            var dhhId = UUID.randomUUID();
            when(participant.getMemberOrHandlerId()).thenReturn(dhhId);
            when(dhh.getId()).thenReturn(dhhId);
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, ExamParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of(participant));
            when(dogHasHandlerReferenceRepository.findById(dhhId)).thenReturn(Optional.of(dhh));

            var examV2Set = baseEventMigrationService.mapToExamV2();

            assertThat(examV2Set)
                    .singleElement()
                    .satisfies(examV2 -> {
                        assertV2(examV2);
                        assertThat(examV2.getParticipants())
                                .singleElement()
                                .satisfies(s -> assertEquals(dhhId, s.getId()));
                    });

        }

        @Test
        void daily() {
            when(exam.getDailyOption()).thenReturn(mock(DailyEventOption.class, Answers.RETURNS_DEEP_STUBS));
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, ExamParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of());

            var examV2Set = baseEventMigrationService.mapToExamV2();

            assertThat(examV2Set)
                    .singleElement()
                    .satisfies(examV2 -> {
                        assertV2(examV2);
                        assertEquals(BaseRecurrenceOptionType.DAILY, examV2.getRecurrenceOption().getOptionType());
                    });
        }

        @Test
        void weekly() {
            when(exam.getWeeklyOption()).thenReturn(mock(WeeklyEventOption.class, Answers.RETURNS_DEEP_STUBS));
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, ExamParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of());

            var examV2Set = baseEventMigrationService.mapToExamV2();

            assertThat(examV2Set)
                    .singleElement()
                    .satisfies(examV2 -> {
                        assertV2(examV2);
                        assertEquals(BaseRecurrenceOptionType.WEEKLY, examV2.getRecurrenceOption().getOptionType());
                    });
        }
    }

    @Nested
    class MapToCourseV2 {
        @Mock
        private Course course;

        @BeforeEach
        void setUp() {
            when(course.getMemberId()).thenReturn(memberId);
            when(course.getId()).thenReturn(id);
            when(baseEventRepository.findAllByEventType(Course.EVENT_TYPE)).thenReturn(Set.of(course));
        }

        @Test
        void mapSpaces() {
            var space = mock(Space.class);
            var spaceDhh = mock(DogHasHandlerReference.class, Answers.RETURNS_DEEP_STUBS);
            var spaceDhhId = UUID.randomUUID();
            when(space.getMemberOrHandlerId()).thenReturn(spaceDhhId);
            when(spaceDhh.getId()).thenReturn(spaceDhhId);
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, Space.PARTICIPANT_TYPE)).thenReturn(Set.of(space));
            when(dogHasHandlerReferenceRepository.findById(spaceDhhId)).thenReturn(Optional.of(spaceDhh));
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of());

            var courseV2Set = baseEventMigrationService.mapToCourseV2();

            assertThat(courseV2Set)
                    .singleElement()
                    .satisfies(courseV2 -> {
                        assertV2(courseV2);
                        assertThat(courseV2.getParticipants())
                                .singleElement()
                                .satisfies(s -> assertEquals(spaceDhhId, s.getId()));
                    });

        }

        @Test
        void mapCoTrainer() {
            var coTrainer = mock(CoTrainer.class, Answers.RETURNS_DEEP_STUBS);
            var coTrainerMember = mock(MemberReference.class);
            var coTrainerMemberId = UUID.randomUUID();
            when(coTrainer.getMemberOrHandlerId()).thenReturn(coTrainerMemberId);
            when(coTrainerMember.getId()).thenReturn(coTrainerMemberId);
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of(coTrainer));
            when(memberReferenceRepository.findById(coTrainerMemberId)).thenReturn(Optional.of(coTrainerMember));
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, Space.PARTICIPANT_TYPE)).thenReturn(Set.of());

            var courseV2Set = baseEventMigrationService.mapToCourseV2();

            assertThat(courseV2Set)
                    .singleElement()
                    .satisfies(courseV2 -> {
                        assertV2(courseV2);
                        assertThat(courseV2.getCoTrainers())
                                .singleElement()
                                .satisfies(c -> assertEquals(coTrainerMemberId, c.getId()));
                    });
        }

        @Test
        void daily() {
            when(course.getDailyOption()).thenReturn(mock(DailyEventOption.class, Answers.RETURNS_DEEP_STUBS));
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of());
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, Space.PARTICIPANT_TYPE)).thenReturn(Set.of());

            var courseV2Set = baseEventMigrationService.mapToCourseV2();

            assertThat(courseV2Set)
                    .singleElement()
                    .satisfies(courseV2 -> {
                        assertV2(courseV2);
                        assertEquals(BaseRecurrenceOptionType.DAILY, courseV2.getRecurrenceOption().getOptionType());
                    });
        }

        @Test
        void weekly() {
            when(course.getWeeklyOption()).thenReturn(mock(WeeklyEventOption.class, Answers.RETURNS_DEEP_STUBS));
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of());
            when(baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, Space.PARTICIPANT_TYPE)).thenReturn(Set.of());

            var courseV2Set = baseEventMigrationService.mapToCourseV2();

            assertThat(courseV2Set)
                    .singleElement()
                    .satisfies(courseV2 -> {
                        assertV2(courseV2);
                        assertEquals(BaseRecurrenceOptionType.WEEKLY, courseV2.getRecurrenceOption().getOptionType());
                    });
        }

    }
}
