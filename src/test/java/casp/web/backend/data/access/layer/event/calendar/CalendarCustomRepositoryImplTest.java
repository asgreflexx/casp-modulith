package casp.web.backend.data.access.layer.event.calendar;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.member.MemberRepository;
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
        var event = TestFixture.createEvent();
        var course = TestFixture.createCourse();

        yesterday = TestFixture.createCalendarEntry(event);
        yesterday.setEventFrom(yesterday.getEventFrom().minusDays(1));
        yesterday.setEventTo(yesterday.getEventTo().minusDays(1));

        today = TestFixture.createCalendarEntry(event);
        event.setMinLocalDateTime(yesterday.getEventFrom());
        event.setMaxLocalDateTime(today.getEventTo());

        tomorrow = TestFixture.createCalendarEntry(course);
        tomorrow.setEventFrom(tomorrow.getEventFrom().plusDays(1));
        tomorrow.setEventTo(tomorrow.getEventTo().plusDays(1));
        course.setMinLocalDateTime(tomorrow.getEventFrom());
        course.setMaxLocalDateTime(tomorrow.getEventTo());

        memberRepository.saveAll(Set.of(event.getMember(), course.getMember()));
        eventRepository.saveAll(Set.of(event, course));
        calendarRepository.saveAll(Set.of(tomorrow, today, yesterday));
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
