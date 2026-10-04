package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Event;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventMapper extends BaseEventMapper<Event, EventDto> {
    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);
}
