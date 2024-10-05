package casp.web.backend.business.logic.layer.event.participants;


import java.util.UUID;

public interface BaseParticipantObserver {

    void deleteParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);

    void deactivateParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);

    void activateParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);
}
