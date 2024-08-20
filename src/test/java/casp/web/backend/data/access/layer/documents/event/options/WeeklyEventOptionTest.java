package casp.web.backend.data.access.layer.documents.event.options;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WeeklyEventOptionTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var weeklyEventOption = new WeeklyEventOption();
        weeklyEventOption.setStartRecurrence(LocalDate.MIN);
        weeklyEventOption.setEndRecurrence(LocalDate.MAX);
        weeklyEventOption.setOccurrences(Set.of(createValidWeeklyEventOptionRecurrence()));

        assertThat(getViolations(weeklyEventOption)).isEmpty();
    }
}
