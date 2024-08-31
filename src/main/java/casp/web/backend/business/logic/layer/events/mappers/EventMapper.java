package casp.web.backend.business.logic.layer.events.mappers;

import casp.web.backend.business.logic.layer.events.dtos.EventDto;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import org.mapstruct.Mapper;

@Mapper
public interface EventMapper extends BaseEventMapper<Event, EventDto> {

}
