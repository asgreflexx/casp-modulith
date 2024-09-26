package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Event;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventMapper extends BaseEventMapper<Event, EventDto> {
    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);
}
