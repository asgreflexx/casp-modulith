package casp.web.backend.business.logic.layer.event.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseEventObserverImplTest {
    @Spy
    private CourseService courseService;
    @Spy
    private EventService eventService;
    @Spy
    private ExamService examService;

    private UUID memberId;
    @InjectMocks
    private BaseEventObserverImpl observer;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
    }

    @Test
    void deleteBaseEventsByMemberId() {
        observer.deleteBaseEventsByMemberId(memberId);

        verify(courseService).deleteBaseEventsByMemberId(memberId);
        verify(eventService).deleteBaseEventsByMemberId(memberId);
        verify(examService).deleteBaseEventsByMemberId(memberId);
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        observer.deactivateBaseEventsByMemberId(memberId);

        verify(courseService).deactivateBaseEventsByMemberId(memberId);
        verify(eventService).deactivateBaseEventsByMemberId(memberId);
        verify(examService).deactivateBaseEventsByMemberId(memberId);
    }

    @Test
    void activateBaseEventsByMemberId() {
        observer.activateBaseEventsByMemberId(memberId);

        verify(courseService).activateBaseEventsByMemberId(memberId);
        verify(eventService).activateBaseEventsByMemberId(memberId);
        verify(examService).activateBaseEventsByMemberId(memberId);
    }

    @Test
    void getCalendarEntriesBetweenFromAndTo() {
        var from = LocalDateTime.now();
        var to = from.plusHours(1);
        var courseCalendarEntryDto = mock(CalendarEntryDto.class);
        var eventCalendarEntryDto = mock(CalendarEntryDto.class);
        var examCalendarEntryDto = mock(CalendarEntryDto.class);
        when(courseService.getCalendarEntriesBetweenFromAndTo(from, to)).thenReturn(Set.of(courseCalendarEntryDto));
        when(eventService.getCalendarEntriesBetweenFromAndTo(from, to)).thenReturn(Set.of(eventCalendarEntryDto));
        when(examService.getCalendarEntriesBetweenFromAndTo(from, to)).thenReturn(Set.of(examCalendarEntryDto));

        var calendarEntryDtoSet = observer.getCalendarEntriesBetweenFromAndTo(from, to);

        assertThat(calendarEntryDtoSet)
                .containsExactlyInAnyOrder(courseCalendarEntryDto, eventCalendarEntryDto, examCalendarEntryDto);
    }
}
