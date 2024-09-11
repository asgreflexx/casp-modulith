package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.presentation.layer.dtos.event.types.BaseEventDto;

interface BaseEventFacade<D extends BaseEventDto<?>> {
    D mapBaseEventToDto(BaseEvent baseEvent);
}
