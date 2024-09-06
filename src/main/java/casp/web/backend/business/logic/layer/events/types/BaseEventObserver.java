package casp.web.backend.business.logic.layer.events.types;

import java.util.UUID;

public interface BaseEventObserver {
    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);
}
