package casp.web.backend.presentation.layer.dtos.event.calendar;

import casp.web.backend.data.access.layer.event.calendar.Calendar;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CalendarMapper {
    CalendarMapper CALENDAR_MAPPER = Mappers.getMapper(CalendarMapper.class);
    Calendar toDocument(CalendarDto dto);

    CalendarDto toDto(Calendar document);
}
