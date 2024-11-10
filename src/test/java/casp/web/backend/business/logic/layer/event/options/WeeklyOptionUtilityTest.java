package casp.web.backend.business.logic.layer.event.options;


import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.WeeklyOption;
import casp.web.backend.data.access.layer.event.options.WeeklyRecurrenceOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeeklyOptionUtilityTest {
    private static final LocalDate START_RECURRENCE = LocalDate.of(2024, 1, 1);
    private static final CalendarEntry EXPECTED_CALENDAR_ENTRY = new CalendarEntry();

    @BeforeEach
    void setUp() {
        EXPECTED_CALENDAR_ENTRY.setEntryFrom(LocalDateTime.of(START_RECURRENCE, LocalTime.of(10, 0, 0)));
        EXPECTED_CALENDAR_ENTRY.setEntryTo(LocalDateTime.of(START_RECURRENCE, LocalTime.of(11, 0, 0)));
    }

    @Test
    void create4CalendarEntriesEveryWeek() {
        var occurrences = new ArrayList<WeeklyOption>();
        occurrences.add(createRecurrence(DayOfWeek.MONDAY, 10, 11));
        occurrences.add(createRecurrence(DayOfWeek.MONDAY, 12, 13));
        var option = createWeeklyEventOption(1, START_RECURRENCE.plusDays(6), occurrences);

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option);

        assertEquals(2, calendarEntries.size());
        assertCalendarEntry(calendarEntries.getFirst());
        EXPECTED_CALENDAR_ENTRY.setEntryFrom(LocalDateTime.of(START_RECURRENCE, LocalTime.of(12, 0, 0)));
        EXPECTED_CALENDAR_ENTRY.setEntryTo(LocalDateTime.of(START_RECURRENCE, LocalTime.of(13, 0, 0)));
        assertCalendarEntry(calendarEntries.get(1));
    }

    @Test
    void create8CalendarEntriesEvery2Weeks() {
        var occurrences = new ArrayList<WeeklyOption>();
        occurrences.add(createRecurrence(DayOfWeek.MONDAY, 10, 11));
        occurrences.add(createRecurrence(DayOfWeek.SUNDAY, 10, 11));
        var option = createWeeklyEventOption(2, START_RECURRENCE.plusDays(106), occurrences);

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option);

        assertEquals(16, calendarEntries.size());
        IntStream.range(0, 8).forEach(i -> {
            assertCalendarEntry(calendarEntries.get(i * 2));

            EXPECTED_CALENDAR_ENTRY.setEntryFrom(EXPECTED_CALENDAR_ENTRY.getEntryFrom().plusDays(6));
            EXPECTED_CALENDAR_ENTRY.setEntryTo(EXPECTED_CALENDAR_ENTRY.getEntryTo().plusDays(6));
            assertCalendarEntry(calendarEntries.get(i * 2 + 1));

            EXPECTED_CALENDAR_ENTRY.setEntryFrom(EXPECTED_CALENDAR_ENTRY.getEntryFrom().plusDays(8));
            EXPECTED_CALENDAR_ENTRY.setEntryTo(EXPECTED_CALENDAR_ENTRY.getEntryTo().plusDays(8));
        });
    }

    private WeeklyRecurrenceOption createWeeklyEventOption(int repeatEvery, LocalDate endRecurrence, List<WeeklyOption> occurrences) {
        var weeklyEventOption = new WeeklyRecurrenceOption();
        weeklyEventOption.setStartRecurrence(START_RECURRENCE);
        weeklyEventOption.setEndRecurrence(endRecurrence);
        weeklyEventOption.setRepeatEvery(repeatEvery);
        weeklyEventOption.setOccurrences(occurrences);
        return weeklyEventOption;

    }

    private WeeklyOption createRecurrence(DayOfWeek dayOfWeek, int startHour, int endHour) {
        var weeklyOption = new WeeklyOption();
        weeklyOption.setDayOfWeek(dayOfWeek);
        weeklyOption.setStartTime(LocalTime.of(startHour, 0, 0));
        weeklyOption.setEndTime(LocalTime.of(endHour, 0, 0));
        return weeklyOption;
    }

    private void assertCalendarEntry(CalendarEntry calendarEntry) {
        assertEquals(EXPECTED_CALENDAR_ENTRY.getEntryFrom(), calendarEntry.getEntryFrom());
        assertEquals(EXPECTED_CALENDAR_ENTRY.getEntryTo(), calendarEntry.getEntryTo());
    }
}
