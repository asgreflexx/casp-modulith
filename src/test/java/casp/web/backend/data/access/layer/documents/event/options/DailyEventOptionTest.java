package casp.web.backend.data.access.layer.documents.event.options;

import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class DailyEventOptionTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var dailyEventOption = new DailyEventOption();
        dailyEventOption.setStartRecurrence(LocalDate.MIN);
        dailyEventOption.setEndRecurrence(LocalDate.MAX);
        dailyEventOption.setStartTime(LocalTime.MIN);
        dailyEventOption.setEndTime(LocalTime.MAX);

        assertThat(getViolations(dailyEventOption)).isEmpty();
    }
}
