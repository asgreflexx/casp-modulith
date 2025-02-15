package casp.web.backend.calendar.options;


import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.WeeklyOption;
import casp.web.backend.calendar.data.options.WeeklyRecurrenceOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class WeeklyOptionUtilityTest {
    private static final LocalDate START_RECURRENCE = LocalDate.of(2024, 1, 1);
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
            var endRecurrence = LocalDate.of(2024, 1, 8);
            option.setStartRecurrence(START_RECURRENCE);
            option.setEndRecurrence(endRecurrence);

            option.setOccurrences(List.of(monday10Clock, monday11Clock));

            var expectedCalendarEntries = List.of(createExpectedCalendarEntry(START_RECURRENCE, monday10Clock),
                    createExpectedCalendarEntry(START_RECURRENCE, monday11Clock),
                    createExpectedCalendarEntry(endRecurrence, monday10Clock),
                    createExpectedCalendarEntry(endRecurrence, monday11Clock));

            var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option);

            assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
        }

        @Test
        void twoEveryTwoWeeks() {
            var endRecurrence = LocalDate.of(2024, 1, 15);
            option.setEndRecurrence(endRecurrence);
            option.setRepeatEvery(2);

            var expectedCalendarEntries = List.of(createExpectedCalendarEntry(START_RECURRENCE, monday10Clock),
                    createExpectedCalendarEntry(START_RECURRENCE, monday11Clock),
                    createExpectedCalendarEntry(endRecurrence, monday10Clock),
                    createExpectedCalendarEntry(endRecurrence, monday11Clock));

            var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option);

            assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
        }

    }

    @Test
    void tuesdayAndWednesday() {
        var tuesday = createWeeklyOption(10, 11, DayOfWeek.TUESDAY);
        var wednesday = createWeeklyOption(11, 12, DayOfWeek.WEDNESDAY);
        var endRecurrence = LocalDate.of(2024, 1, 15);
        option.setOccurrences(List.of(tuesday, wednesday));
        option.setEndRecurrence(endRecurrence);
        var expectedCalendarEntries = List.of(createExpectedCalendarEntry(LocalDate.of(2024, 1, 2), tuesday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 3), wednesday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 9), tuesday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 10), wednesday));

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option);

        assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
    }

    @Test
    void mondayAndSunday() {
        var monday = createWeeklyOption(10, 11, DayOfWeek.MONDAY);
        var sunday = createWeeklyOption(11, 12, DayOfWeek.SUNDAY);
        var endRecurrence = LocalDate.of(2024, 1, 15);
        option.setOccurrences(List.of(monday, sunday));
        option.setEndRecurrence(endRecurrence);
        var expectedCalendarEntries = List.of(createExpectedCalendarEntry(LocalDate.of(2024, 1, 1), monday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 7), sunday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 8), monday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 14), sunday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 15), monday));

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option);

        assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
    }

    @Test
    void mondayAndSundayEveryTwoWeeks() {
        var monday = createWeeklyOption(10, 11, DayOfWeek.MONDAY);
        var sunday = createWeeklyOption(11, 12, DayOfWeek.SUNDAY);
        var endRecurrence = LocalDate.of(2024, 1, 15);
        option.setOccurrences(List.of(monday, sunday));
        option.setEndRecurrence(endRecurrence);
        option.setRepeatEvery(2);
        var expectedCalendarEntries = List.of(createExpectedCalendarEntry(LocalDate.of(2024, 1, 1), monday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 7), sunday),
                createExpectedCalendarEntry(LocalDate.of(2024, 1, 15), monday));

        var calendarEntries = WeeklyOptionUtility.createCalendarEntries(option);

        assertThat(calendarEntries).zipSatisfy(expectedCalendarEntries, WeeklyOptionUtilityTest::assertCalendarEntry);
    }

    private static WeeklyOption createWeeklyOption(final int startHour, final int endHour, final DayOfWeek dayOfWeek) {
        var weeklyOption = new WeeklyOption();
        weeklyOption.setDayOfWeek(dayOfWeek);
        weeklyOption.setStartTime(LocalTime.of(startHour, 0, 0));
        weeklyOption.setEndTime(LocalTime.of(endHour, 0, 0));
        return weeklyOption;
    }

    private static CalendarEntry createExpectedCalendarEntry(final LocalDate date, final WeeklyOption option) {
        return new CalendarEntry(LocalDateTime.of(date, option.getStartTime()), LocalDateTime.of(date, option.getEndTime()));
    }

    private static void assertCalendarEntry(final CalendarEntry actual, final CalendarEntry expected) {
        assertEquals(0, actual.compareTo(expected),
                "Actual is not same as the expected calendar entry");
    }

}
