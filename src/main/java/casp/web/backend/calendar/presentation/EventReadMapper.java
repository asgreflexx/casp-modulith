package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.EventDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventReadMapper extends BaseViewMapper<EventDto, EventRead> {
    EventReadMapper EVENT_READ_MAPPER = Mappers.getMapper(EventReadMapper.class);
}
