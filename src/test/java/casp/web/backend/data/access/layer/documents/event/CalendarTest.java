package casp.web.backend.data.access.layer.documents.event;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalendarTest extends BaseEntityTest {

    private Calendar calendar;

    @BeforeEach
    void setUp() {
        calendar = new Calendar();
        calendar.setEventFrom(LocalDateTime.now());
        calendar.setEventTo(LocalDateTime.now().plusHours(1));

        calendar.setBaseEvent(createValidEvent());
    }

    @Test
    void happyPath() {
        assertThat(getViolations(calendar)).isEmpty();
        baseAssertions(calendar);
    }

    @Test
    void sort() {
        var previousCalendarEntry = new Calendar();
        previousCalendarEntry.setEventFrom(LocalDateTime.now().minusHours(2));
        previousCalendarEntry.setEventTo(LocalDateTime.now().minusHours(1));
        var nextCalendarEntry = new Calendar();
        nextCalendarEntry.setEventFrom(LocalDateTime.now().plusHours(2));
        nextCalendarEntry.setEventTo(LocalDateTime.now().plusHours(3));

        List<Calendar> actualSort = new ArrayList<>(List.of(nextCalendarEntry, previousCalendarEntry, calendar));
        Collections.sort(actualSort);

        assertThat(actualSort).containsExactly(previousCalendarEntry, calendar, nextCalendarEntry);
    }
}
