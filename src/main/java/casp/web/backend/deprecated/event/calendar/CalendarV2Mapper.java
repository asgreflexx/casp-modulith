package casp.web.backend.deprecated.event.calendar;

import casp.web.backend.calendar.data.CalendarEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface CalendarV2Mapper {
    CalendarV2Mapper CALENDAR_V2_MAPPER = Mappers.getMapper(CalendarV2Mapper.class);

    @Mapping(target = "entryFrom", source = "eventFrom")
    @Mapping(target = "entryTo", source = "eventTo")
    CalendarEntry toCalendarEntry(Calendar calendar);

    List<CalendarEntry> toCalendarEntryList(List<Calendar> calendarList);
}
