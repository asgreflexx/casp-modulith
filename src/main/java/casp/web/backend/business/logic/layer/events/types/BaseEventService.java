package casp.web.backend.business.logic.layer.events.types;


import casp.web.backend.business.logic.layer.events.dtos.BaseEventDto;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BaseEventService<E extends BaseEvent, P extends BaseParticipant, D extends BaseEventDto<P>> {

    D saveBaseEventDto(D actualBaseEventDto);

    void deleteBaseEventById(UUID id);

    D getBaseEventDtoById(UUID id);

    D createNewBaseEventWithOneCalendarEntry();

    Page<E> getBaseEventsAsPage(int year, Pageable pageable);

    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);
}
