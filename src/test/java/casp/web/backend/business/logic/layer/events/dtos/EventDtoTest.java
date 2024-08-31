package casp.web.backend.business.logic.layer.events.dtos;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.events.mappers.EventMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventDtoTest {

    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        var event = TestFixture.createValidEvent();
        eventDto = new EventMapperImpl().documentToDto(event);
        eventDto.setCalendarEntries(List.of(TestFixture.createValidCalendarEntry(event)));
    }

    @Test
    void happyPath() {
        assertThat(TestFixture.getViolations(eventDto)).isEmpty();
    }

    @Test
    void participantsIsNull() {
        eventDto.setParticipants(null);

        assertThat(TestFixture.getViolations(eventDto)).hasSize(1);
    }
}
