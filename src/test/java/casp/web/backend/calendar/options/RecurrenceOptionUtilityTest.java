package casp.web.backend.calendar.options;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import casp.web.backend.calendar.data.options.WeeklyOption;
import casp.web.backend.calendar.data.options.WeeklyRecurrenceOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RecurrenceOptionUtilityTest {
    private static final LocalTime START_TIME = LocalTime.of(1, 0, 0);
    private static final LocalTime END_TIME = LocalTime.of(2, 0, 0);
    private static final LocalDate RECURRENCE = LocalDate.of(2024, 1, 1);
    private static final CalendarEntry EXPECTED_CALENDAR_ENTRY = new CalendarEntry();

    @BeforeEach
    void setUp() {
        EXPECTED_CALENDAR_ENTRY.setEntryFrom(RECURRENCE.atTime(START_TIME));
        EXPECTED_CALENDAR_ENTRY.setEntryTo(RECURRENCE.atTime(END_TIME));
    }

    @Test
    void withDailyOption() {
        var option = new DailyRecurrenceOption();
        option.setStartRecurrence(RECURRENCE);
        option.setEndRecurrence(RECURRENCE);
        option.setStartTime(START_TIME);
        option.setEndTime(END_TIME);

        assertThat(RecurrenceOptionUtility.createCalendarEntries(option))
                .singleElement()
                .satisfies(this::assertCalendarEntry);
    }

    @Test
    void withWeeklyOption() {
        List<WeeklyOption> occurrences = new ArrayList<>();
        var weeklyOption = new WeeklyOption();
        weeklyOption.setDayOfWeek(DayOfWeek.MONDAY);
        weeklyOption.setStartTime(START_TIME);
        weeklyOption.setEndTime(END_TIME);
        occurrences.add(weeklyOption);

        var option = new WeeklyRecurrenceOption();
        option.setStartRecurrence(RECURRENCE);
        option.setEndRecurrence(RECURRENCE);
        option.setOccurrences(occurrences);

        assertThat(RecurrenceOptionUtility.createCalendarEntries(option))
                .singleElement()
                .satisfies(this::assertCalendarEntry);
    }

    private void assertCalendarEntry(CalendarEntry calendarEntry) {
        assertEquals(EXPECTED_CALENDAR_ENTRY.getEntryFrom(), calendarEntry.getEntryFrom());
        assertEquals(EXPECTED_CALENDAR_ENTRY.getEntryTo(), calendarEntry.getEntryTo());
    }
}
