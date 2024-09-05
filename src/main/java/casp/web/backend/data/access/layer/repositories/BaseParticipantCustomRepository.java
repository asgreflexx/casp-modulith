package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;

import java.util.Set;
import java.util.UUID;

public interface BaseParticipantCustomRepository {
    <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdIn(Set<UUID> memberOrHandlersId, P newInstanceOfBaseParticipant);

    <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdAndEntityStatus(UUID memberOrHandlerId, EntityStatus entityStatus);

    <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdAndEntityStatusNot(UUID memberOrHandlerId, EntityStatus entityStatus);
}