package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.base.BaseDtoMapper;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import org.mapstruct.Mapping;

public interface BaseEventMapper<S extends BaseEvent, T extends BaseEventDto> extends BaseDtoMapper<S, T> {
    @Mapping(target = "calendarEntries", ignore = true)
    @Override
    S toSource(T source);
}
