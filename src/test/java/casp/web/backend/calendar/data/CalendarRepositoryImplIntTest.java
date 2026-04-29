package casp.web.backend.calendar.data;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.CalendarFixture;
import casp.web.backend.common.enums.EntityStatus;
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

            assertThat(calendarEntryProjections).zipSatisfy(List.of(exam, event, course), (calendarEntryProjection, baseEvent) -> {
                assertThat(calendarEntryProjection.id()).isEqualTo(baseEvent.getId());
                assertThat(calendarEntryProjection.name()).isEqualTo(baseEvent.getName());
                assertThat(calendarEntryProjection.eventType()).isEqualTo(baseEvent.getEventType());
                assertThat(calendarEntryProjection.calendarEntries()).containsAll(baseEvent.getCalendarEntries());
            });
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

            assertThat(calendarEntryProjections).zipSatisfy(List.of(baseEvent), (calendarEntryProjection, actualBaseEvent) -> {
                assertThat(calendarEntryProjection.id()).isEqualTo(actualBaseEvent.getId());
                assertThat(calendarEntryProjection.name()).isEqualTo(actualBaseEvent.getName());
                assertThat(calendarEntryProjection.eventType()).isEqualTo(actualBaseEvent.getEventType());
                assertThat(calendarEntryProjection.calendarEntries()).containsAll(actualBaseEvent.getCalendarEntries());
            });
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
}
