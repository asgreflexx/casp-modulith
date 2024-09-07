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
    protected final String participantType;

    BaseParticipantServiceImpl(final BaseParticipantRepository baseParticipantRepository, final String participantType) {
        this.baseParticipantRepository = baseParticipantRepository;
        this.participantType = participantType;
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

    @Override
    public void deleteParticipantsByBaseEventId(final UUID baseEventId) {
        baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNot(baseEventId, EntityStatus.DELETED)
                .forEach(participant -> saveItWithNewStatus(participant, EntityStatus.DELETED));
    }

    @Override
    public void deactivateParticipantsByBaseEventId(final UUID baseEventId) {
        getParticipantsByBaseEventId(baseEventId)
                .forEach(participant -> saveItWithNewStatus(participant, EntityStatus.INACTIVE));
    }

    @Override
    public void activateParticipantsByBaseEventId(final UUID baseEventId) {
        baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(baseEventId, EntityStatus.INACTIVE)
                .forEach(participant -> saveItWithNewStatus(participant, EntityStatus.ACTIVE));
    }

    @Override
    public void deleteParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(memberOrHandlerId, EntityStatus.DELETED, participantType)
                .forEach(participant -> saveItWithNewStatus(participant, EntityStatus.DELETED));
    }

    @Override
    public void deactivateParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(memberOrHandlerId, EntityStatus.ACTIVE, participantType)
                .forEach(participant -> saveItWithNewStatus(participant, EntityStatus.INACTIVE));
    }

    @Override
    public void activateParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(memberOrHandlerId, EntityStatus.INACTIVE, participantType)
                .forEach(participant -> saveItWithNewStatus(participant, EntityStatus.ACTIVE));
    }

    private void saveItWithNewStatus(final BaseParticipant participant, final EntityStatus entityStatus) {
        participant.setEntityStatus(entityStatus);
        baseParticipantRepository.save(participant);
    }
}
