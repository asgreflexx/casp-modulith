package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.BaseEventObserver;
import casp.web.backend.calendar.CalendarEntryDto;
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
    private BaseEventObserver baseEventObserver;
    @Mock
    private CalendarEntryDto calendarEntryDto;

    private CalendarRestController calendarRestController;

    @BeforeEach
    void setUp() {
        calendarRestController = new CalendarRestController(baseEventObserver, ZONE_ID);
    }

    @Test
    void getCalendarEntries() {
        var from = createLocalDate(0);
        var to = createLocalDate(1);
        var atStartOfDay = from.atStartOfDay().atZone(ZONE_ID).toOffsetDateTime();
        var atEndOfDay = to.atTime(LocalTime.MAX).atZone(ZONE_ID).toOffsetDateTime();
        when(baseEventObserver.getCalendarEntriesBetweenFromAndToOrMemberId(atStartOfDay, atEndOfDay, null)).thenReturn(List.of(calendarEntryDto));

        var calendarEntries = calendarRestController.getCalendarEntries(from, to, null);

        assertThat(calendarEntries.getBody())
                .containsExactly(calendarEntryDto);
    }
}
