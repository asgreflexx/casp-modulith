package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.Event;
import casp.web.backend.calendar.data.Exam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static casp.web.backend.calendar.CalendarFixture.createCalendarEntryDto;
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
        var courseCalendarEntryDto = createCalendarEntryDto(new Course());
        var from = courseCalendarEntryDto.getEntryFromODT();
        var to = courseCalendarEntryDto.getEntryToODT();
        var eventCalendarEntryDto = createCalendarEntryDto(new Event());
        eventCalendarEntryDto.setEntryFromODT(from.plusHours(2));
        eventCalendarEntryDto.setEntryToODT(from.plusHours(3));
        var examCalendarEntryDto = createCalendarEntryDto(new Exam());
        examCalendarEntryDto.setEntryFromODT(from.plusHours(4));
        examCalendarEntryDto.setEntryToODT(from.plusHours(5));
        when(courseService.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId)).thenReturn(Stream.of(courseCalendarEntryDto));
        when(eventService.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId)).thenReturn(Stream.of(eventCalendarEntryDto));
        when(examService.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId)).thenReturn(Stream.of(examCalendarEntryDto));

        var calendarEntryDtoList = observer.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId);

        assertThat(calendarEntryDtoList)
                .containsExactly(courseCalendarEntryDto, eventCalendarEntryDto, examCalendarEntryDto);
    }
}
