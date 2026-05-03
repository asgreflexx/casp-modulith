package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntryProjection;
import casp.web.backend.calendar.data.CalendarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static casp.web.backend.calendar.CalendarFixture.createCalendarEntry;
import static casp.web.backend.calendar.CalendarFixture.createCalendarEntryPlusDays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {
    @Mock
    private CalendarRepository calendarRepository;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    @Test
    void findCalendarEntriesByFromAndToAndMemberId() {
        var from = OffsetDateTime.now().minusDays(1);
        var to = OffsetDateTime.now().plusDays(1);
        var memberId = UUID.randomUUID();
        var course = new CalendarEntryProjection(UUID.randomUUID(),
                List.of(createCalendarEntry(), createCalendarEntryPlusDays(1)),
                BaseEventType.COURSE,
                "Course");
        var exam = new CalendarEntryProjection(UUID.randomUUID(),
                List.of(createCalendarEntryPlusDays(2), createCalendarEntryPlusDays(3)),
                BaseEventType.EXAM,
                "Exam");
        var projectionList = List.of(course, exam);
        var expectedCalendarEntries = projectionList
                .stream()
                .flatMap(cep -> cep.calendarEntries().stream().map(ce -> new CalendarEntryDto(ce, cep.id(), cep.eventType(), cep.name())))
                .toList();
        when(calendarRepository.findCalendarEntriesByFromAndToAndMemberId(from, to, memberId)).thenReturn(projectionList);

        var calendarEntryDtoList = calendarService.findCalendarEntriesByFromAndToAndMemberId(from, to, memberId);

        assertThat(calendarEntryDtoList).zipSatisfy(expectedCalendarEntries,
                (actual, expected) ->
                        assertThat(actual).usingRecursiveAssertion().isEqualTo(expected));
    }
}
