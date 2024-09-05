package casp.web.backend.business.logic.layer.events.participants;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

abstract class BaseParticipantServiceImpl<P extends BaseParticipant, E extends BaseEvent> implements BaseParticipantService<P, E> {
    protected final BaseParticipantRepository baseParticipantRepository;

    BaseParticipantServiceImpl(final BaseParticipantRepository baseParticipantRepository) {
        this.baseParticipantRepository = baseParticipantRepository;
    }

    @Transactional
    @Override
    public Set<P> saveParticipants(final Set<P> participants, final E baseEvent) {
        baseParticipantRepository.deleteAllByBaseEventId(baseEvent.getId());
        participants.forEach(participant -> participant.setBaseEvent(baseEvent));
        return new HashSet<>(baseParticipantRepository.saveAll(participants));
    }

    // The casting is correct
    @SuppressWarnings("unchecked")
    @Override
    public Set<P> getParticipantsByBaseEventId(final UUID baseEventId) {
        return (Set<P>) baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(baseEventId, EntityStatus.ACTIVE);
    }

    @Transactional
    @Override
    public void deleteParticipantsByBaseEventId(final UUID baseEventId) {
        baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNot(baseEventId, EntityStatus.DELETED)
                .forEach(participant -> participant.setEntityStatus(EntityStatus.DELETED));
    }

    @Transactional
    @Override
    public void deactivateParticipantsByBaseEventId(final UUID baseEventId) {
        getParticipantsByBaseEventId(baseEventId)
                .forEach(participant -> participant.setEntityStatus(EntityStatus.INACTIVE));
    }

    @Transactional
    @Override
    public void activateParticipantsByBaseEventId(final UUID baseEventId) {
        baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(baseEventId, EntityStatus.INACTIVE)
                .forEach(participant -> participant.setEntityStatus(EntityStatus.ACTIVE));
    }

    @Transactional
    @Override
    public void deleteParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(memberOrHandlerId, EntityStatus.DELETED)
                .forEach(participant -> participant.setEntityStatus(EntityStatus.DELETED));
    }

    @Transactional
    @Override
    public void deactivateParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(memberOrHandlerId, EntityStatus.ACTIVE)
                .forEach(participant -> participant.setEntityStatus(EntityStatus.INACTIVE));
    }

    @Transactional
    @Override
    public void activateParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(memberOrHandlerId, EntityStatus.INACTIVE)
                .forEach(participant -> participant.setEntityStatus(EntityStatus.ACTIVE));
    }
}