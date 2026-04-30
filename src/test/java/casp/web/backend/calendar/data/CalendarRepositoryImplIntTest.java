package casp.web.backend.calendar.data;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.CalendarFixture;
import casp.web.backend.calendar.data.participants.BaseParticipant;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.EventParticipant;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static casp.web.backend.calendar.CalendarFixture.createCalendarEntry;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class CalendarRepositoryImplIntTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private CalendarRepositoryImpl calendarRepository;
    private OffsetDateTime fromDateTime;
    private OffsetDateTime toDateTime;
    private Course course;
    private Event event;
    private Exam exam;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        eventRepository.deleteAll();
        examRepository.deleteAll();
        dogHasHandlerReferenceRepository.deleteAll();
        memberReferenceRepository.deleteAll();

        var calendarEntry = createCalendarEntry();
        fromDateTime = calendarEntry.getEntryFromODT().minusDays(2);
        toDateTime = calendarEntry.getEntryToODT().plusDays(2);
        course = courseRepository.save(initializeEventWithCalendar(new Course(), 1));
        event = eventRepository.save(initializeEventWithCalendar(new Event(), 0));
        exam = examRepository.save(initializeEventWithCalendar(new Exam(), -1));
    }

    @Nested
    class FindCalendarEntriesByFromAndToAndMemberId {
        @Test
        void memberIdIsNull() {
            var calendarEntryProjections = calendarRepository.findCalendarEntriesByFromAndToAndMemberId(fromDateTime, toDateTime, null);

            assertCalendarEntryProjections(calendarEntryProjections, List.of(exam, event, course));
        }

        @ParameterizedTest
        @EnumSource(BaseEventType.class)
        void memberIdIsNotNull(BaseEventType baseEventType) {
            var baseEvent = switch (baseEventType) {
                case COURSE -> course;
                case EVENT -> event;
                default -> exam;
            };

            var calendarEntryProjections = calendarRepository.findCalendarEntriesByFromAndToAndMemberId(fromDateTime, toDateTime, baseEvent.getMember().getId());

            assertCalendarEntryProjections(calendarEntryProjections, List.of(baseEvent));
        }

        @ParameterizedTest
        @EnumSource(value = EntityStatus.class, names = {"DELETED", "INACTIVE"})
        void eventsAreNotActive(EntityStatus entityStatus) {
            course.setEntityStatus(entityStatus);
            event.setEntityStatus(entityStatus);
            exam.setEntityStatus(entityStatus);
            courseRepository.save(course);
            eventRepository.save(event);
            examRepository.save(exam);

            var calendarEntryProjections = calendarRepository.findCalendarEntriesByFromAndToAndMemberId(fromDateTime, toDateTime, null);

            assertThat(calendarEntryProjections).isEmpty();
        }

        @Test
        void badRange() {
            var calendarEntryProjections = calendarRepository.findCalendarEntriesByFromAndToAndMemberId(toDateTime, fromDateTime, null);

            assertThat(calendarEntryProjections).isEmpty();
        }

        @Nested
        class FindByParticipant {

            @Test
            void course() {
                var dogHasHandlerReference = createDogHasHandlerReference();
                course.addParticipants(Set.of(new Space(dogHasHandlerReference)));
                courseRepository.save(course);

                var calendarEntryProjections = calendarRepository.findCalendarEntriesByFromAndToAndMemberId(fromDateTime, toDateTime, dogHasHandlerReference.getMember().getId());

                assertCalendarEntryProjections(calendarEntryProjections, List.of(course));
            }

            @Test
            void event() {
                var memberReference = createMemberReference();
                event.addParticipants(Set.of(new EventParticipant(memberReference)));
                eventRepository.save(event);

                var calendarEntryProjections = calendarRepository.findCalendarEntriesByFromAndToAndMemberId(fromDateTime, toDateTime, memberReference.getId());

                assertCalendarEntryProjections(calendarEntryProjections, List.of(event));
            }

            @Test
            void exam() {
                var dogHasHandlerReference = createDogHasHandlerReference();
                exam.addParticipants(Set.of(new ExamParticipant(dogHasHandlerReference)));
                examRepository.save(exam);

                var calendarEntryProjections = calendarRepository.findCalendarEntriesByFromAndToAndMemberId(fromDateTime, toDateTime, dogHasHandlerReference.getMember().getId());

                assertCalendarEntryProjections(calendarEntryProjections, List.of(exam));
            }
        }

        @Test
        void FindByCoTrainer() {
            var memberReference = createMemberReference();
            course.addCoTrainers(Set.of(new CoTrainer(memberReference)));
            courseRepository.save(course);

            var calendarEntryProjections = calendarRepository.findCalendarEntriesByFromAndToAndMemberId(fromDateTime, toDateTime, memberReference.getId());

            assertCalendarEntryProjections(calendarEntryProjections, List.of(course));
        }

        private void assertCalendarEntryProjections(List<CalendarEntryProjection> calendarEntryProjections, List<BaseEvent<? extends BaseParticipant>> expectedEvents) {
            assertThat(calendarEntryProjections).zipSatisfy(expectedEvents, (calendarEntryProjection, baseEvent) -> {
                assertThat(calendarEntryProjection.id()).isEqualTo(baseEvent.getId());
                assertThat(calendarEntryProjection.name()).isEqualTo(baseEvent.getName());
                assertThat(calendarEntryProjection.eventType()).isEqualTo(baseEvent.getEventType());
                assertThat(calendarEntryProjection.calendarEntries()).containsAll(baseEvent.getCalendarEntries());
            });
        }
    }

    private <E extends BaseEvent<?>> E initializeEventWithCalendar(E baseEvent, int plusDays) {
        baseEvent.setName(baseEvent.getEventType().name());
        baseEvent.setMember(createMemberReference());
        baseEvent.addCalendarEntry(CalendarFixture.createCalendarEntryPlusDays(plusDays));
        baseEvent.addCalendarEntry(CalendarFixture.createCalendarEntryPlusDays(plusDays - 2));
        baseEvent.addCalendarEntry(CalendarFixture.createCalendarEntryPlusDays(plusDays + 2));
        return baseEvent;
    }

    private MemberReference createMemberReference() {
        var member = ReferenceTestFixture.createMemberReference();
        return memberReferenceRepository.save(member);
    }

    private DogHasHandlerReference createDogHasHandlerReference() {
        var dogHasHandlerReference = ReferenceTestFixture.createDogHasHandlerReference();
        return dogHasHandlerReferenceRepository.save(dogHasHandlerReference);
    }
}
