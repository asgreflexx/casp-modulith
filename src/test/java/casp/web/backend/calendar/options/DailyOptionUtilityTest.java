package casp.web.backend.calendar.options;


import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyOptionUtilityTest {

    private static final LocalTime START_TIME = LocalTime.of(1, 0, 0);
    private static final LocalTime END_TIME = LocalTime.of(2, 0, 0);
    private static final LocalDate START_RECURRENCE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_RECURRENCE = START_RECURRENCE.plusDays(9);
    private static final CalendarEntry EXPECTED_CALENDAR_ENTRY = new CalendarEntry();

    @BeforeEach
    void setUp() {
        EXPECTED_CALENDAR_ENTRY.setEntryFrom(LocalDateTime.of(START_RECURRENCE, START_TIME));
        EXPECTED_CALENDAR_ENTRY.setEntryTo(LocalDateTime.of(START_RECURRENCE, END_TIME));
    }

    @Test
    void create10CalendarEntriesEveryDay() {
        var repeatEvery = 1;

        var calendarEntries = DailyOptionUtility.createCalendarEntries(createDailyOption(repeatEvery));

        assertThat(calendarEntries)
                .hasSize(10)
                .allSatisfy(calendarEntry -> assertCalendarEntry(calendarEntry, repeatEvery));
    }

    @Test
    void create5CalendarEntriesEverySecondDay() {
        var repeatEvery = 2;
        createDailyOption(repeatEvery);

        var calendarEntries = DailyOptionUtility.createCalendarEntries(createDailyOption(repeatEvery));

        assertThat(calendarEntries)
                .hasSize(5)
                .allSatisfy(calendarEntry -> assertCalendarEntry(calendarEntry, repeatEvery));
    }

    private DailyRecurrenceOption createDailyOption(int repeatEvery) {
        var daily = new DailyRecurrenceOption();
        daily.setStartRecurrence(START_RECURRENCE);
        daily.setEndRecurrence(END_RECURRENCE);
        daily.setStartTime(START_TIME);
        daily.setEndTime(END_TIME);
        daily.setRepeatEvery(repeatEvery);
        return daily;
    }

    private void assertCalendarEntry(CalendarEntry calendarEntry, int repeat) {
        assertEquals(EXPECTED_CALENDAR_ENTRY.getEntryFrom(), calendarEntry.getEntryFrom());
        assertEquals(EXPECTED_CALENDAR_ENTRY.getEntryTo(), calendarEntry.getEntryTo());

        EXPECTED_CALENDAR_ENTRY.setEntryFrom(EXPECTED_CALENDAR_ENTRY.getEntryFrom().plusDays(repeat));
        EXPECTED_CALENDAR_ENTRY.setEntryTo(EXPECTED_CALENDAR_ENTRY.getEntryTo().plusDays(repeat));
    }
}
