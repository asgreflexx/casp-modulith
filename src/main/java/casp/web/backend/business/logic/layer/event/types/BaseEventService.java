package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.data.access.layer.event.types.BaseEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BaseEventService<E extends BaseEvent> {

    /**
     * It sets the member, before saving it.
     *
     * @param actualBaseEvent instance of BaseEvent
     * @return saved instance of BaseEvent
     */
    E save(E actualBaseEvent);

    void deleteById(UUID id);

    E getOneById(UUID id);

    Page<E> getAllByYear(int year, Pageable pageable);

    /**
     * Set all base events with the given memberId to deleted status.
     * This is used when a member is deleted.
     *
     * @param memberId the id of the member
     */
    void deleteBaseEventsByMemberId(UUID memberId);

    /**
     * Set all base events with the given memberId to inactive status.
     * This is used when a member is deactivated.
     *
     * @param memberId the id of the member
     */
    void deactivateBaseEventsByMemberId(UUID memberId);

    /**
     * Set all base events with the given memberId to active status.
     * This is used when a member is activated.
     *
     * @param memberId the id of the member
     */
    void activateBaseEventsByMemberId(UUID memberId);
}
