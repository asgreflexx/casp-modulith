package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.CalendarEntryDto;
import casp.web.backend.calendar.CalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static casp.web.backend.calendar.CalendarFixture.ZONE_ID;
import static casp.web.backend.calendar.CalendarFixture.createLocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarRestControllerTest {
    @Mock
    private CalendarService calendarService;
    @Mock
    private CalendarEntryDto calendarEntryDto;

    private CalendarRestController calendarRestController;

    @BeforeEach
    void setUp() {
        calendarRestController = new CalendarRestController(ZONE_ID, calendarService);
    }

    @Test
    void getCalendarEntries() {
        var from = createLocalDate(0);
        var to = createLocalDate(1);
        var atStartOfDay = from.atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();
        var atEndOfDay = to.atTime(LocalTime.MAX).atZone(ZONE_ID).toOffsetDateTime();
        when(calendarService.findCalendarEntriesByFromAndToAndMemberId(atStartOfDay, atEndOfDay, null)).thenReturn(List.of(calendarEntryDto));

        var calendarEntries = calendarRestController.getCalendarEntries(from, to, null);

        assertThat(calendarEntries.getBody())
                .containsExactly(calendarEntryDto);
    }
}
