package casp.web.backend.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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
    void getCalendarEntriesBetweenFromAndToOrMemberId() {
        var from = LocalDateTime.now();
        var to = from.plusDays(1);
        var courseCalendarEntryDto = new CalendarEntryDto();
        courseCalendarEntryDto.setEntryFrom(from);
        courseCalendarEntryDto.setEntryTo(from.plusHours(1));
        var eventCalendarEntryDto = new CalendarEntryDto();
        eventCalendarEntryDto.setEntryFrom(from.plusHours(2));
        eventCalendarEntryDto.setEntryTo(from.plusHours(3));
        var examCalendarEntryDto = new CalendarEntryDto();
        examCalendarEntryDto.setEntryFrom(from.plusHours(4));
        examCalendarEntryDto.setEntryTo(from.plusHours(5));
        when(courseService.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId)).thenReturn(Stream.of(courseCalendarEntryDto));
        when(eventService.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId)).thenReturn(Stream.of(eventCalendarEntryDto));
        when(examService.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId)).thenReturn(Stream.of(examCalendarEntryDto));

        var calendarEntryDtoList = observer.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId);

        assertThat(calendarEntryDtoList)
                .containsExactly(courseCalendarEntryDto, eventCalendarEntryDto, examCalendarEntryDto);
    }
}
