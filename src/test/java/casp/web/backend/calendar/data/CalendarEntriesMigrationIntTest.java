package casp.web.backend.calendar.data;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Deprecated(forRemoval = true, since = "2026-04-23")
@Testcontainers
@SpringBootTest
class CalendarEntriesMigrationIntTest {
    private static final LocalDateTime FIRST_OF_JANUARY = LocalDateTime.of(2023, 1, 1, 0, 0);
    private static final LocalDateTime FIRST_OF_MAY = LocalDateTime.of(2023, 5, 1, 0, 0);
    private static final ZoneOffset OFFSET_1_HOUR = ZoneOffset.of("+01:00");
    private static final ZoneOffset OFFSET_2_HOURS = ZoneOffset.of("+02:00");
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
    private CalendarEntriesMigration calendarEntriesMigration;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        eventRepository.deleteAll();
        examRepository.deleteAll();
        memberReferenceRepository.deleteAll();

    }

    @Test
    void migrate() {
        courseRepository.save(createCourse(createCalendarEntry()));
        eventRepository.save(createEvent(createCalendarEntry()));
        examRepository.save(createExam(createCalendarEntry()));

        calendarEntriesMigration.migrate();

        var baseEvents = Stream.of(courseRepository, eventRepository, examRepository)
                .flatMap(r -> r.findAll().stream())
                .toList();
        var actualCalendarEntries = baseEvents
                .stream()
                .flatMap(e -> e.getCalendarEntries().stream())
                .toList();
        assertThat(actualCalendarEntries)
                .hasSize(3)
                .allSatisfy(CalendarEntriesMigrationIntTest::assertCalendarEntry);
    }

    private static CalendarEntry createCalendarEntry() {
        var calendarEntry = new CalendarEntry();
        calendarEntry.setEntryFrom(FIRST_OF_JANUARY);
        calendarEntry.setEntryTo(FIRST_OF_MAY);
        return calendarEntry;
    }

    private static void assertCalendarEntry(CalendarEntry ce) {
        assertOffsetDateTime(ce.getEntryFromODT(), OFFSET_1_HOUR, FIRST_OF_JANUARY);
        assertOffsetDateTime(ce.getEntryToODT(), OFFSET_2_HOURS, FIRST_OF_MAY);
    }

    private static void assertOffsetDateTime(OffsetDateTime offsetDateTime, ZoneOffset zoneOffset, LocalDateTime localDateTime) {
        Assertions.assertThat(offsetDateTime)
                .satisfies(entry -> {
                    assertThat(entry.getOffset()).isEqualTo(zoneOffset);
                    assertThat(entry.toInstant()).isEqualTo(localDateTime.toInstant(zoneOffset));
                });
    }

    private Course createCourse(CalendarEntry localCalendarEntry) {
        return initializeEventWithEntry(localCalendarEntry, new Course());
    }

    private Event createEvent(CalendarEntry localCalendarEntry) {
        return initializeEventWithEntry(localCalendarEntry, new Event());
    }

    private Exam createExam(CalendarEntry localCalendarEntry) {
        return initializeEventWithEntry(localCalendarEntry, new Exam());
    }

    private <T extends BaseEvent<?>> T initializeEventWithEntry(CalendarEntry localCalendarEntry, T baseEvent) {
        baseEvent.setMember(createMemberReference());
        baseEvent.addCalendarEntry(localCalendarEntry);
        return baseEvent;
    }

    private MemberReference createMemberReference() {
        var member = ReferenceTestFixture.createMemberReference();
        return memberReferenceRepository.save(member);
    }
}
