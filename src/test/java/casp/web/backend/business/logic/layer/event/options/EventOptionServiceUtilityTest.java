package casp.web.backend.business.logic.layer.event.options;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOptionRecurrence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class EventOptionServiceUtilityTest {
    private static final LocalTime START_TIME = LocalTime.of(1, 0, 0);
    private static final LocalTime END_TIME = LocalTime.of(2, 0, 0);
    private static final LocalDate RECURRENCE = LocalDate.of(2024, 1, 1);
    private Calendar expectedCalendarEntry;

    @BeforeEach
    void setUp() {
        expectedCalendarEntry = TestFixture.createValidCalendarEntry();
        expectedCalendarEntry.setLocation("Wien");
        expectedCalendarEntry.setEventFrom(RECURRENCE.atTime(START_TIME));
        expectedCalendarEntry.setEventTo(RECURRENCE.atTime(END_TIME));
    }

    @Test
    void withoutEventOption() {
        assertThat(EventOptionServiceUtility.createCalendarEntries(expectedCalendarEntry.getBaseEvent(), expectedCalendarEntry))
                .singleElement()
                .satisfies(calendarEntry -> {
                    assertSame(expectedCalendarEntry.getBaseEvent(), calendarEntry.getBaseEvent());
                    assertEquals(expectedCalendarEntry.getLocation(), calendarEntry.getLocation());
                    assertEquals(expectedCalendarEntry.getEventFrom(), calendarEntry.getEventFrom());
                    assertEquals(expectedCalendarEntry.getEventTo(), calendarEntry.getEventTo());
                });
    }

    @Test
    void withDailyOption() {
        var dailyEventOption = TestFixture.createValidDailyEventOption();
        dailyEventOption.setStartRecurrence(RECURRENCE);
        dailyEventOption.setEndRecurrence(RECURRENCE);
        dailyEventOption.setStartTime(START_TIME);
        dailyEventOption.setEndTime(END_TIME);
        expectedCalendarEntry.getBaseEvent().setDailyOption(dailyEventOption);

        assertThat(EventOptionServiceUtility.createCalendarEntries(expectedCalendarEntry.getBaseEvent(), expectedCalendarEntry))
                .singleElement()
                .satisfies(calendarEntry -> {
                    assertSame(expectedCalendarEntry.getBaseEvent(), calendarEntry.getBaseEvent());
                    assertEquals(expectedCalendarEntry.getLocation(), calendarEntry.getLocation());
                    assertEquals(expectedCalendarEntry.getEventFrom(), calendarEntry.getEventFrom());
                    assertEquals(expectedCalendarEntry.getEventTo(), calendarEntry.getEventTo());
                });
    }

    @Test
    void withWeeklyOption() {
        List<WeeklyEventOptionRecurrence> occurrences = new ArrayList<>();
        var weeklyEventOptionRecurrence = new WeeklyEventOptionRecurrence();
        weeklyEventOptionRecurrence.setDayOfWeek(DayOfWeek.MONDAY);
        weeklyEventOptionRecurrence.setStartTime(START_TIME);
        weeklyEventOptionRecurrence.setEndTime(END_TIME);
        occurrences.add(weeklyEventOptionRecurrence);

        var weeklyEventOption = TestFixture.createValidWeeklyEventOption();
        weeklyEventOption.setStartRecurrence(RECURRENCE);
        weeklyEventOption.setEndRecurrence(RECURRENCE);
        weeklyEventOption.setOccurrences(occurrences);
        expectedCalendarEntry.getBaseEvent().setWeeklyOption(weeklyEventOption);

        assertThat(EventOptionServiceUtility.createCalendarEntries(expectedCalendarEntry.getBaseEvent(), expectedCalendarEntry))
                .singleElement()
                .satisfies(calendarEntry -> {
                    assertSame(expectedCalendarEntry.getBaseEvent(), calendarEntry.getBaseEvent());
                    assertEquals(expectedCalendarEntry.getLocation(), calendarEntry.getLocation());
                    assertEquals(expectedCalendarEntry.getEventFrom(), calendarEntry.getEventFrom());
                    assertEquals(expectedCalendarEntry.getEventTo(), calendarEntry.getEventTo());
                });
    }
}
