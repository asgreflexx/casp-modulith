package casp.web.backend.presentation.layer.dtos;

import casp.web.backend.TestFixture;
import casp.web.backend.presentation.layer.dtos.event.types.EventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.EventMapper.EVENT_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;

class EventDtoTest {

    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        var event = TestFixture.createValidEvent();
        eventDto = EVENT_MAPPER.toDto(event);
        eventDto.setCalendarEntries(CALENDAR_MAPPER.toDtoList(List.of(TestFixture.createValidCalendarEntry(event))));
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
