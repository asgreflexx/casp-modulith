package casp.web.backend.calendar;

import java.util.UUID;

interface BaseEventService<T extends BaseEventDto<?>> {
    void save(T dto);

    void deleteById(UUID id);

    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);

    T getOneById(UUID id);
}
