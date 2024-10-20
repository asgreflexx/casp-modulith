package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.MemberReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class EventCustomRepositoryImplTest {
    @Autowired
    private EventRepository eventRepository;
    private Event event;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        event = new Event();
        event.setMember(new MemberReference());
        event = eventRepository.save(event);
    }

    @Test
    void findAllByMemberIdAndNotDeleted() {
        var eventSet = eventRepository.findAllByMemberIdAndNotDeleted(event.getMember().getId());

        assertThat(eventSet)
                .containsExactly(event);
    }

    @Test
    void findAllByMemberIdAndStatus() {
        var eventSet = eventRepository.findAllByMemberIdAndStatus(event.getMember().getId(), EntityStatus.ACTIVE);

        assertThat(eventSet)
                .containsExactly(event);
    }
}
