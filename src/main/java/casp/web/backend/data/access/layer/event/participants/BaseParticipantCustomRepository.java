package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;

import java.util.Set;
import java.util.UUID;

public interface BaseParticipantCustomRepository {
    <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdIn(Set<UUID> memberOrHandlersId, final String participantType);

    <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdAndEntityStatus(UUID memberOrHandlerId, EntityStatus entityStatus, final String participantType);

    <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdAndEntityStatusNot(UUID memberOrHandlerId, EntityStatus entityStatus, final String participantType);
}
