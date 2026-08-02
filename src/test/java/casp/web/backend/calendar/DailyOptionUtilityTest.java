package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import static casp.web.backend.calendar.CalendarFixture.ZONE_ID;
import static casp.web.backend.calendar.CalendarFixture.createFirstMondayTheMonth;
import static casp.web.backend.calendar.CalendarFixture.createLocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyOptionUtilityTest {

    private static final LocalTime START_TIME = LocalTime.of(1, 0, 0);
    private static final LocalTime END_TIME = LocalTime.of(2, 0, 0);
    private static final LocalDate START_RECURRENCE = createFirstMondayTheMonth();
    private static final LocalDate END_RECURRENCE = createLocalDate(9);
    private OffsetDateTime entryFromODT;
    private OffsetDateTime entryToODT;

    @BeforeEach
    void setUp() {
        entryFromODT = LocalDateTime.of(START_RECURRENCE, START_TIME).atZone(ZONE_ID).toOffsetDateTime();
        entryToODT = LocalDateTime.of(START_RECURRENCE, END_TIME).atZone(ZONE_ID).toOffsetDateTime();
    }

    @Test
    void create10CalendarEntriesEveryDay() {
        var repeatEvery = 1;

        var calendarEntries = DailyOptionUtility.createCalendarEntries(createDailyOption(repeatEvery), ZONE_ID);

        assertThat(calendarEntries)
                .hasSize(10)
                .allSatisfy(calendarEntry -> assertCalendarEntry(calendarEntry, repeatEvery));
    }

    @Test
    void create5CalendarEntriesEverySecondDay() {
        var repeatEvery = 2;

        var calendarEntries = DailyOptionUtility.createCalendarEntries(createDailyOption(repeatEvery), ZONE_ID);

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
        assertEquals(entryFromODT, calendarEntry.getEntryFromODT());
        assertEquals(entryToODT, calendarEntry.getEntryToODT());

        entryFromODT = entryFromODT.plusDays(repeat);
        entryToODT = entryToODT.plusDays(repeat);
    }
}
