package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.presentation.layer.dtos.event.types.BaseEventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

interface BaseEventFacade<D extends BaseEventDto<?>> {
    /**
     * Map document to dto.
     * Set participants, spaces or coTrainers to the baseEvents
     *
     * @param baseEvent instance of BaseEvent
     * @return a dto with participants, spaces or coTrainers
     */
    D mapDocumentToDto(BaseEvent baseEvent);

    void save(D dto);

    D getOneById(UUID id);

    void deleteById(UUID id);

    Page<D> getAllByYear(int year, Pageable pageable);
}
