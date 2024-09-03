package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CalendarCustomRepositoryImplTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BaseEventRepository eventRepository;
    @Autowired
    private CalendarRepository calendarRepository;
    private Calendar yesterday;
    private Calendar today;
    private Calendar tomorrow;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        eventRepository.deleteAll();
        calendarRepository.deleteAll();
        var event = TestFixture.createValidEvent();
        var course = TestFixture.createValidCourse();

        yesterday = TestFixture.createValidCalendarEntry(event);
        yesterday.setEventFrom(yesterday.getEventFrom().minusDays(1));
        yesterday.setEventTo(yesterday.getEventTo().minusDays(1));

        today = TestFixture.createValidCalendarEntry(event);
        event.setMinLocalDateTime(yesterday.getEventFrom());
        event.setMaxLocalDateTime(today.getEventTo());

        tomorrow = TestFixture.createValidCalendarEntry(course);
        tomorrow.setEventFrom(tomorrow.getEventFrom().plusDays(1));
        tomorrow.setEventTo(tomorrow.getEventTo().plusDays(1));
        course.setMinLocalDateTime(tomorrow.getEventFrom());
        course.setMaxLocalDateTime(tomorrow.getEventTo());

        memberRepository.saveAll(Set.of(event.getMember(), course.getMember()));
        eventRepository.saveAll(Set.of(event, course));
        calendarRepository.saveAll(Set.of(tomorrow, today, yesterday));
    }

    @Test
    void findAllByBaseEventId() {
        assertThat(calendarRepository.findAllByBaseEventId(yesterday.getBaseEvent().getId()))
                .containsExactly(yesterday, today);
    }

    @Nested
    class FindAllBetweenEventFromAndEventToAndEventTypes {
        @Test
        void withoutEventTypes() {
            assertThat(calendarRepository.findAllBetweenEventFromAndEventToAndEventTypes(yesterday.getEventFrom().toLocalDate(), tomorrow.getEventTo().toLocalDate(), null))
                    .containsExactly(yesterday, today, tomorrow);
        }

        @Test
        void withEventType() {
            assertThat(calendarRepository.findAllBetweenEventFromAndEventToAndEventTypes(yesterday.getEventFrom().toLocalDate(), tomorrow.getEventTo().toLocalDate(), Set.of(Event.EVENT_TYPE)))
                    .containsExactly(yesterday, today);
        }

        @Test
        void onlyFromYesterday() {
            assertThat(calendarRepository.findAllBetweenEventFromAndEventToAndEventTypes(yesterday.getEventFrom().toLocalDate(), yesterday.getEventTo().toLocalDate(), null))
                    .containsExactly(yesterday);
        }
    }
}
