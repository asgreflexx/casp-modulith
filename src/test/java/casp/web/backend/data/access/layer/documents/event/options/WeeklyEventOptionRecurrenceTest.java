package casp.web.backend.data.access.layer.documents.event.options;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseDocumentTest;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WeeklyEventOptionRecurrenceTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var weeklyEventOptionRecurrence = TestFixture.createValidWeeklyEventOptionRecurrence();

        assertThat(TestFixture.getViolations(weeklyEventOptionRecurrence)).isEmpty();
        assertNotNull(weeklyEventOptionRecurrence.getId());
    }

    @Test
    void sort() {
        var first = TestFixture.createValidWeeklyEventOptionRecurrence();
        first.setEndTime(LocalTime.MIN.plusHours(1));
        var second = TestFixture.createValidWeeklyEventOptionRecurrence();
        second.setStartTime(LocalTime.MIN.plusHours(2));
        second.setEndTime(LocalTime.MIN.plusHours(3));
        var third = TestFixture.createValidWeeklyEventOptionRecurrence();
        third.setStartTime(LocalTime.MIN.plusHours(4));
        third.setEndTime(LocalTime.MIN.plusHours(5));
        var actualSort = new ArrayList<>(List.of(third, first, second));
        Collections.sort(actualSort);

        assertThat(actualSort).containsExactly(first, second, third);
    }
}
