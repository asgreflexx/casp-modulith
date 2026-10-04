package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import casp.web.backend.calendar.data.options.WeeklyOption;
import casp.web.backend.calendar.data.options.WeeklyRecurrenceOption;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RecurrenceOptionUtilityTest {
    private static final LocalTime START_TIME = LocalTime.of(1, 0, 0);
    private static final LocalTime END_TIME = LocalTime.of(2, 0, 0);
    private static final LocalDate RECURRENCE = LocalDate.of(2024, JANUARY, 1);
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
    private static final CalendarEntry EXPECTED_CALENDAR_ENTRY = new CalendarEntry(OffsetDateTime.of(RECURRENCE, START_TIME, ZONE_OFFSET), OffsetDateTime.of(RECURRENCE, END_TIME, ZONE_OFFSET));

    @Test
    void withDailyOption() {
        var option = new DailyRecurrenceOption();
        option.setStartRecurrence(RECURRENCE);
        option.setEndRecurrence(RECURRENCE);
        option.setStartTime(START_TIME);
        option.setEndTime(END_TIME);

        assertThat(RecurrenceOptionUtility.createCalendarEntries(option, ZONE_OFFSET))
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

        assertThat(RecurrenceOptionUtility.createCalendarEntries(option, ZONE_OFFSET))
                .singleElement()
                .satisfies(this::assertCalendarEntry);
    }

    private void assertCalendarEntry(CalendarEntry calendarEntry) {
        assertEquals(EXPECTED_CALENDAR_ENTRY.getEntryFromODT(), calendarEntry.getEntryFromODT());
        assertEquals(EXPECTED_CALENDAR_ENTRY.getEntryToODT(), calendarEntry.getEntryToODT());
    }
}
