package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BaseEventService<E extends BaseEvent> {

    E saveBaseEvent(E actualBaseEventDto);

    void deleteBaseEventById(UUID id);

    E getBaseEventById(UUID id);

    Page<E> getBaseEventsAsPage(int year, Pageable pageable);

    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);
}
