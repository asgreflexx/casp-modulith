package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.common.base.BaseDtoMapper;
import org.mapstruct.Mapping;

public interface BaseEventMapper<S extends BaseEvent, T extends BaseEventDto> extends BaseDtoMapper<S, T> {
    @Mapping(target = "calendarEntries", ignore = true)
    @Override
    S toSource(T source);
}
