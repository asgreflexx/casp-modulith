package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.WeeklyOption;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public enum CalendarFixture {
    ;
    private static final LocalDate NOW = LocalDate.now();
    private static final int ACTUAL_MONTH = NOW.getMonthValue();
    public static final int ACTUAL_YEAR = NOW.getYear();
    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Vienna");

    public static CalendarEntry createCalendarEntry() {
        return createCalendarEntryPlusYears(0);
    }

    public static CalendarEntry createCalendarEntryPlusDays(int plusDays) {
        var calendarEntry = createCalendarEntry();
        calendarEntry.setEntryFromODT(calendarEntry.getEntryFromODT().plusDays(plusDays));
        calendarEntry.setEntryToODT(calendarEntry.getEntryToODT().plusDays(plusDays));
        return calendarEntry;
    }

    public static CalendarEntry createCalendarEntryPlusYears(int plusYears) {
        var year = ACTUAL_YEAR + plusYears;
        var entryFromODT = LocalDateTime.of(year, ACTUAL_MONTH, 1, 10, 0)
                .atZone(ZONE_ID)
                .toOffsetDateTime();
        var entryToODT = LocalDateTime.of(year, ACTUAL_MONTH, 1, 12, 0)
                .atZone(ZONE_ID)
                .toOffsetDateTime();
        return new CalendarEntry(entryFromODT, entryToODT);
    }

    /**
     * Creates a test date by adding a specified number of days to the first Monday of the current month.
     * <p>
     * This method is useful for generating test dates relative to the first Monday of the month,
     * which is commonly used as a reference point in calendar-related unit tests.
     *
     * @param plusDays the number of days to add to the first Monday of the month (can be negative to go back in time)
     * @return a {@link LocalDate} representing the calculated date
     */
    public static LocalDate createLocalDate(int plusDays) {
        var firstMondayOfTheYear = createFirstMondayTheMonth();
        return firstMondayOfTheYear.plusDays(plusDays);
    }

    static NewCalendarEntryDto createNewCalendarEntryDto() {
        var calendarEntry = createCalendarEntry();
        var newCalendarEntryDto = new NewCalendarEntryDto();
        newCalendarEntryDto.setEntryFromODT(calendarEntry.getEntryFromODT());
        newCalendarEntryDto.setEntryToODT(calendarEntry.getEntryToODT());
        return newCalendarEntryDto;
    }

    static LocalDate createFirstMondayTheMonth() {
        var localDate = LocalDate.of(ACTUAL_YEAR, ACTUAL_MONTH, 1);
        return localDate.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }

    static CalendarEntryDto createCalendarEntryDto(BaseEvent<?> baseEvent) {
        return new CalendarEntryDto(createCalendarEntry(), baseEvent);
    }

    static CalendarEntry createCalendarEntry(LocalDate date, WeeklyOption option) {
        var startDateTime = LocalDateTime.of(date, option.getStartTime()).atZone(ZONE_ID).toOffsetDateTime();
        var endDateTime = LocalDateTime.of(date, option.getEndTime()).atZone(ZONE_ID).toOffsetDateTime();
        return new CalendarEntry(startDateTime, endDateTime);
    }
}
