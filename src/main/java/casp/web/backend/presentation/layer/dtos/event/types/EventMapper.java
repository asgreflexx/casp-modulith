package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventMapper extends BaseMapper<Event, EventDto> {
    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);
}
