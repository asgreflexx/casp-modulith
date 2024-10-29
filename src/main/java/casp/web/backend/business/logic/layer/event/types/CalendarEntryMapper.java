package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.types.BaseEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CalendarEntryMapper {
    CalendarEntryMapper CALENDAR_MAPPER = Mappers.getMapper(CalendarEntryMapper.class);

    CalendarEntryDto fromBaseEvent(BaseEvent baseEvent);
}
