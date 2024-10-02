package casp.web.backend.data.access.layer.event.calendar;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalendarTest extends BaseDocumentTest {
    @Test
    void sort() {
        var nowCalendarEntry = TestFixture.createCalendarEntry();
        var previousCalendarEntry = new Calendar();
        previousCalendarEntry.setEventFrom(LocalDateTime.now().minusHours(2));
        previousCalendarEntry.setEventTo(LocalDateTime.now().minusHours(1));
        var nextCalendarEntry = new Calendar();
        nextCalendarEntry.setEventFrom(LocalDateTime.now().plusHours(2));
        nextCalendarEntry.setEventTo(LocalDateTime.now().plusHours(3));

        List<Calendar> actualSort = new ArrayList<>(List.of(nextCalendarEntry, previousCalendarEntry, nowCalendarEntry));
        Collections.sort(actualSort);

        assertThat(actualSort).containsExactly(previousCalendarEntry, nowCalendarEntry, nextCalendarEntry);
    }
}
