package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.EventDto;
import casp.web.backend.calendar.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static casp.web.backend.calendar.presentation.EventReadMapper.EVENT_READ_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRestControllerTest {
    @Mock
    private EventService eventService;

    @InjectMocks
    private EventRestController eventRestController;

    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setName("Test Event");
    }

    @Test
    void save() {
        var eventWrite = mock(EventWrite.class);
        when(eventWrite.getName()).thenReturn("event");

        var response = eventRestController.save(eventWrite);

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService).save(argThat(actualEventDto -> eventWrite.getName().equals(actualEventDto.getName())));
    }

    @Test
    void deleteById() {
        var response = eventRestController.deleteById(eventDto.getId());

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService).deleteById(eventDto.getId());
    }

    @Test
    void getCalendarEntry() {
        var calendarEntryId = UUID.randomUUID();
        when(eventService.getOneByIdAndCalendarEntryId(eventDto.getId(), calendarEntryId)).thenReturn(eventDto);

        var response = eventRestController.getCalendarEntry(eventDto.getId(), calendarEntryId);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertEquals(EVENT_READ_MAPPER.toTarget(eventDto), response.getBody());
    }

    @Test
    void migrateDataToV2() {
        var response = eventRestController.migrateDataToV2();

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService).migrateDataToV2();
    }
}
