package casp.web.backend.calendar.data;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.participants.EventParticipant;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest
class EventCustomRepositoryImplTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        memberReferenceRepository.deleteAll();
    }

    @Nested
    class FindAllByMemberId {
        private Event event1;
        private Event event2;
        private CalendarEntry calendarEntry;

        @BeforeEach
        void setUp() {
            calendarEntry = new CalendarEntry();
            calendarEntry.setEntryFrom(LocalDateTime.of(2024, 1, 1, 0, 0));
            calendarEntry.setEntryTo(calendarEntry.getEntryFrom().plusHours(10));
            event1 = eventRepository.save(createEvent("Event1"));
            event2 = eventRepository.save(createEvent("Event2"));
        }

        @Test
        void memberIdIsNull() {
            var actualEvents = eventRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), null);

            assertThat(actualEvents).containsExactlyInAnyOrder(event1, event2);
        }

        @Test
        void memberIdIsTheSameAsEventMember() {
            var actualEvents = eventRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), event2.member.getId());

            assertThat(actualEvents).containsExactly(event2);
        }

        @Test
        void memberIdIsTheSameAsEventParticipant() {
            var participantMemberId = event2.getParticipants().stream().map(ep -> ep.getMember().getId()).toList().getFirst();

            var actualEvents = eventRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), participantMemberId);

            assertThat(actualEvents).containsExactly(event2);
        }

        private Event createEvent(String name) {
            var event = new Event();
            event.setName(name);
            event.setMember(createMemberReference());
            event.addCalendarEntry(calendarEntry);
            event.addParticipants(Set.of(new EventParticipant(createMemberReference())));
            return eventRepository.save(event);
        }

        private MemberReference createMemberReference() {
            var member = ReferenceTestFixture.createMemberReference();
            return memberReferenceRepository.save(member);
        }
    }
}
