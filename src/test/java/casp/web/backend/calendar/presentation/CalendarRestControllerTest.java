package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.BaseEventObserver;
import casp.web.backend.calendar.CalendarEntryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarRestControllerTest {
    @Mock
    private BaseEventObserver baseEventObserver;
    @Mock
    private CalendarEntryDto calendarEntryDto;

    @InjectMocks
    private CalendarRestController calendarRestController;

    @Test
    void getCalendarEntries() {
        when(baseEventObserver.getCalendarEntriesBetweenFromAndTo(LocalDateTime.MIN, LocalDateTime.MAX, Set.of())).thenReturn(Stream.of(calendarEntryDto));

        var calendarEntries = calendarRestController.getCalendarEntries(LocalDate.MIN, LocalDate.MAX, Set.of());

        assertThat(calendarEntries.getBody())
                .containsExactly(calendarEntryDto);
    }
}
