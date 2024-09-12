package casp.web.backend.business.logic.layer.event.options;


import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOptionRecurrence;
import casp.web.backend.data.access.layer.event.types.Event;
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
    private static final String LOCATION = "Wien";
    private static final LocalDate START_RECURRENCE = LocalDate.of(2024, 1, 1);
    private static final Event EVENT = TestFixture.createValidEvent();
    private Calendar expectedCalendarEntry;

    @BeforeEach
    void setUp() {
        expectedCalendarEntry = TestFixture.createValidCalendarEntry(EVENT);
        expectedCalendarEntry.setEventFrom(LocalDateTime.of(START_RECURRENCE, LocalTime.of(10, 0, 0)));
        expectedCalendarEntry.setEventTo(LocalDateTime.of(START_RECURRENCE, LocalTime.of(11, 0, 0)));
    }

    @Test
    void create4CalendarEntriesEveryWeek() {
        var occurrences = new ArrayList<WeeklyEventOptionRecurrence>();
        occurrences.add(createRecurrence(DayOfWeek.MONDAY, 10, 11));
        occurrences.add(createRecurrence(DayOfWeek.MONDAY, 12, 13));
        createWeeklyEventOption(1, START_RECURRENCE.plusDays(6), occurrences);

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(LOCATION, EVENT);

        assertEquals(2, calendarEntries.size());
        assertCalendarEntry(calendarEntries.getFirst());
        expectedCalendarEntry.setEventFrom(LocalDateTime.of(START_RECURRENCE, LocalTime.of(12, 0, 0)));
        expectedCalendarEntry.setEventTo(LocalDateTime.of(START_RECURRENCE, LocalTime.of(13, 0, 0)));
        assertCalendarEntry(calendarEntries.get(1));
    }

    @Test
    void create8CalendarEntriesEvery2Weeks() {
        var occurrences = new ArrayList<WeeklyEventOptionRecurrence>();
        occurrences.add(createRecurrence(DayOfWeek.MONDAY, 10, 11));
        occurrences.add(createRecurrence(DayOfWeek.SUNDAY, 10, 11));
        createWeeklyEventOption(2, START_RECURRENCE.plusDays(106), occurrences);

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(LOCATION, EVENT);

        assertEquals(16, calendarEntries.size());
        IntStream.range(0, 8).forEach(i -> {
            assertCalendarEntry(calendarEntries.get(i * 2));

            expectedCalendarEntry.setEventFrom(expectedCalendarEntry.getEventFrom().plusDays(6));
            expectedCalendarEntry.setEventTo(expectedCalendarEntry.getEventTo().plusDays(6));
            assertCalendarEntry(calendarEntries.get(i * 2 + 1));

            expectedCalendarEntry.setEventFrom(expectedCalendarEntry.getEventFrom().plusDays(8));
            expectedCalendarEntry.setEventTo(expectedCalendarEntry.getEventTo().plusDays(8));
        });
    }

    private void createWeeklyEventOption(final int repeatEvery, final LocalDate endRecurrence, final List<WeeklyEventOptionRecurrence> occurrences) {
        var weeklyEventOption = TestFixture.createValidWeeklyEventOption();
        weeklyEventOption.setStartRecurrence(START_RECURRENCE);
        weeklyEventOption.setEndRecurrence(endRecurrence);
        weeklyEventOption.setRepeatEvery(repeatEvery);
        weeklyEventOption.setOccurrences(occurrences);

        EVENT.setWeeklyOption(weeklyEventOption);
    }

    private WeeklyEventOptionRecurrence createRecurrence(final DayOfWeek dayOfWeek, final int startHour, final int endHour) {
        var weeklyEventOptionRecurrence = new WeeklyEventOptionRecurrence();
        weeklyEventOptionRecurrence.setDayOfWeek(dayOfWeek);
        weeklyEventOptionRecurrence.setStartTime(LocalTime.of(startHour, 0, 0));
        weeklyEventOptionRecurrence.setEndTime(LocalTime.of(endHour, 0, 0));
        return weeklyEventOptionRecurrence;
    }

    private void assertCalendarEntry(final Calendar calendarEntry) {
        assertEquals(LOCATION, calendarEntry.getLocation());
        assertEquals(expectedCalendarEntry.getEventFrom(), calendarEntry.getEventFrom());
        assertEquals(expectedCalendarEntry.getEventTo(), calendarEntry.getEventTo());
    }
}
