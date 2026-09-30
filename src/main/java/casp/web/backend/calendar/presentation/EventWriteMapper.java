package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.EventDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventWriteMapper extends BaseViewMapper<EventDto, EventWrite> {
    EventWriteMapper EVENT_WRITE_MAPPER = Mappers.getMapper(EventWriteMapper.class);
}
