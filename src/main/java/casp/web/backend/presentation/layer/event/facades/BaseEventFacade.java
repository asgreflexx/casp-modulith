package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.presentation.layer.dtos.event.types.BaseEventDto;

interface BaseEventFacade<D extends BaseEventDto<?>> {
    D mapDocumentToDto(BaseEvent baseEvent);

    void save(D dto);
}
