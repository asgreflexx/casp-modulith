package casp.web.backend.business.logic.layer.events.options;


import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyOptionUtilityTest {

    private static final String LOCATION = "Wien";
    private static final LocalTime START_TIME = LocalTime.of(1, 0, 0);
    private static final LocalTime END_TIME = LocalTime.of(2, 0, 0);
    private static final LocalDate START_RECURRENCE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_RECURRENCE = START_RECURRENCE.plusDays(9);
    private static final Event EVENT = TestFixture.createValidEvent();
    private Calendar expectedCalendarEntry;

    @BeforeEach
    void setUp() {
        expectedCalendarEntry = TestFixture.createValidCalendarEntry(EVENT);
        expectedCalendarEntry.setEventFrom(LocalDateTime.of(START_RECURRENCE, START_TIME));
        expectedCalendarEntry.setEventTo(LocalDateTime.of(START_RECURRENCE, END_TIME));
    }

    @Test
    void create10CalendarEntriesEveryDay() {
        var repeatEvery = 1;
        createDailyOption(repeatEvery);

        var calendarEntries = DailyOptionUtility.createCalendarEntries(LOCATION, EVENT);

        assertThat(calendarEntries)
                .hasSize(10)
                .allSatisfy(calendarEntry -> assertCalendarEntry(calendarEntry, repeatEvery));
    }

    @Test
    void create5CalendarEntriesEverySecondDay() {
        var repeatEvery = 2;
        createDailyOption(repeatEvery);

        var calendarEntries = DailyOptionUtility.createCalendarEntries(LOCATION, EVENT);

        assertThat(calendarEntries)
                .hasSize(5)
                .allSatisfy(calendarEntry -> assertCalendarEntry(calendarEntry, repeatEvery));
    }

    private void createDailyOption(final int repeatEvery) {
        var dailyEventOption = TestFixture.createValidDailyEventOption();
        dailyEventOption.setStartRecurrence(START_RECURRENCE);
        dailyEventOption.setEndRecurrence(END_RECURRENCE);
        dailyEventOption.setStartTime(START_TIME);
        dailyEventOption.setEndTime(END_TIME);
        dailyEventOption.setRepeatEvery(repeatEvery);

        EVENT.setDailyOption(dailyEventOption);
    }

    private void assertCalendarEntry(final Calendar calendarEntry, final int repeat) {
        assertEquals(LOCATION, calendarEntry.getLocation());
        assertEquals(expectedCalendarEntry.getEventFrom(), calendarEntry.getEventFrom());
        assertEquals(expectedCalendarEntry.getEventTo(), calendarEntry.getEventTo());

        expectedCalendarEntry.setEventFrom(expectedCalendarEntry.getEventFrom().plusDays(repeat));
        expectedCalendarEntry.setEventTo(expectedCalendarEntry.getEventTo().plusDays(repeat));
    }
}
