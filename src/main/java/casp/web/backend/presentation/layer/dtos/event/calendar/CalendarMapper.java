package casp.web.backend.presentation.layer.dtos.event.calendar;

import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;

@Mapper
public interface CalendarMapper extends BaseMapper<Calendar, CalendarDto> {
    @Override
    Calendar toDocument(CalendarDto dto);
}
