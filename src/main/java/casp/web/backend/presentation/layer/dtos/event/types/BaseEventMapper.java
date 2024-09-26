package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import java.util.Optional;

public interface BaseEventMapper<E extends BaseEvent, D extends BaseEventDto<?>> extends BaseMapper<E, D> {
    @Override
    D toDto(E document);

    @AfterMapping
    default void afterToDto(@MappingTarget D dto, E document) {
        Optional.ofNullable(document.getDailyOption()).ifPresent(dto::setOption);
        Optional.ofNullable(document.getWeeklyOption()).ifPresent(dto::setOption);
    }
}
