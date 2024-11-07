package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.EventDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventWriteMapper extends BaseViewMapper<EventDto, EventWrite> {
    EventWriteMapper EVENT_WRITE_MAPPER = Mappers.getMapper(EventWriteMapper.class);
}
