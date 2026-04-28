package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.WeeklyOption;
import casp.web.backend.calendar.data.options.WeeklyRecurrenceOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static casp.web.backend.calendar.CalendarFixture.ZONE_ID;
import static casp.web.backend.calendar.CalendarFixture.createCalendarEntry;
import static casp.web.backend.calendar.CalendarFixture.createFirstMondayTheMonth;
import static casp.web.backend.calendar.CalendarFixture.createLocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WeeklyOptionUtilityTest {
    private static final LocalDate START_RECURRENCE = createFirstMondayTheMonth();
    private WeeklyRecurrenceOption option;

    @BeforeEach
    void setUp() {
        option = new WeeklyRecurrenceOption();
        option.setStartRecurrence(START_RECURRENCE);
    }

    @Nested
    class MultipleOccurrencesOnTheSameDay {
        private WeeklyOption monday10Clock;
        private WeeklyOption monday11Clock;

        @BeforeEach
        void setUp() {
            monday10Clock = createWeeklyOption(10, 11, DayOfWeek.MONDAY);
            monday11Clock = createWeeklyOption(11, 12, DayOfWeek.MONDAY);
            option.setOccurrences(List.of(monday10Clock, monday11Clock));
        }

        @Test
        void twoEveryWeek() {
            option.setStartRecurrence(START_RECURRENCE);
            option.setEndRecurrence(createLocalDate(6));
            var expectedCalendarEntries = List.of(createCalendarEntry(START_RECURRENCE, monday10Clock),
                    createCalendarEntry(START_RECURRENCE, monday11Clock));

            option.setOccurrences(List.of(monday10Clock, monday11Clock));

            var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option, ZONE_ID);

            assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
        }

        @Test
        void twoEveryTwoWeeks() {
            option.setEndRecurrence(createLocalDate(13));
            option.setRepeatEvery(2);
            var expectedCalendarEntries = List.of(createCalendarEntry(START_RECURRENCE, monday10Clock),
                    createCalendarEntry(START_RECURRENCE, monday11Clock));

            var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option, ZONE_ID);

            assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
        }

    }

    @Test
    void tuesdayAndWednesday() {
        var tuesday = createWeeklyOption(10, 11, DayOfWeek.TUESDAY);
        var wednesday = createWeeklyOption(11, 12, DayOfWeek.WEDNESDAY);
        var endRecurrence = createLocalDate(6);
        option.setOccurrences(List.of(tuesday, wednesday));
        option.setEndRecurrence(endRecurrence);
        var tuesdayDate = START_RECURRENCE.plusDays(DayOfWeek.TUESDAY.getValue() - 1);
        var wednesdayDate = START_RECURRENCE.plusDays(DayOfWeek.WEDNESDAY.getValue() - 1);
        var expectedCalendarEntries = List.of(createCalendarEntry(tuesdayDate, tuesday),
                createCalendarEntry(wednesdayDate, wednesday));

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option, ZONE_ID);

        assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
    }

    @Test
    void mondayAndSunday() {
        var monday = createWeeklyOption(10, 11, DayOfWeek.MONDAY);
        var sunday = createWeeklyOption(11, 12, DayOfWeek.SUNDAY);
        var endRecurrence = createLocalDate(6);
        option.setOccurrences(List.of(monday, sunday));
        option.setEndRecurrence(endRecurrence);
        var expectedCalendarEntries = List.of(createCalendarEntry(START_RECURRENCE, monday),
                createCalendarEntry(endRecurrence, sunday));

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option, ZONE_ID);

        assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
    }

    @Test
    void mondayAndSundayEveryTwoWeeks() {
        var monday = createWeeklyOption(10, 11, DayOfWeek.MONDAY);
        var sunday = createWeeklyOption(11, 12, DayOfWeek.SUNDAY);
        var endRecurrence = createLocalDate(13);
        option.setOccurrences(List.of(monday, sunday));
        option.setEndRecurrence(endRecurrence);
        option.setRepeatEvery(2);
        var sundayDate = START_RECURRENCE.plusDays(DayOfWeek.SUNDAY.getValue() - 1);
        var expectedCalendarEntries = List.of(createCalendarEntry(START_RECURRENCE, monday),
                createCalendarEntry(sundayDate, sunday));

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option, ZONE_ID);

        assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
    }

    private static WeeklyOption createWeeklyOption(int startHour, int endHour, DayOfWeek dayOfWeek) {
        var weeklyOption = new WeeklyOption();
        weeklyOption.setDayOfWeek(dayOfWeek);
        weeklyOption.setStartTime(LocalTime.of(startHour, 0, 0));
        weeklyOption.setEndTime(LocalTime.of(endHour, 0, 0));
        return weeklyOption;
    }

    private static void assertCalendarEntry(CalendarEntry actual, CalendarEntry expected) {
        assertEquals(0, actual.compareTo(expected),
                "Actual is not same as the expected calendar entry");
    }
}
