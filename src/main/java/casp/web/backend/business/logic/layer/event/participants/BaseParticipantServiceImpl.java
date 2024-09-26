package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.types.BaseEvent;

import java.util.Set;
import java.util.UUID;

abstract class BaseParticipantServiceImpl<P extends BaseParticipant, E extends BaseEvent> implements BaseParticipantService<P, E> {
    protected final BaseParticipantRepository baseParticipantRepository;
    protected final String participantType;

    BaseParticipantServiceImpl(final BaseParticipantRepository baseParticipantRepository, final String participantType) {
        this.baseParticipantRepository = baseParticipantRepository;
        this.participantType = participantType;
    }

    protected void replaceParticipantsAndSetMetadata(final E baseEvent, final Set<P> participants) {
        baseParticipantRepository.deleteAllByBaseEventId(baseEvent.getId());
        baseParticipantRepository.saveAll(participants);
        baseEvent.setParticipantsSize(participants.size());
    }

    // The casting is correct
    @SuppressWarnings("unchecked")
    @Override
    public Set<P> getParticipantsByBaseEventId(final UUID baseEventId) {
        return (Set<P>) baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(baseEventId, EntityStatus.ACTIVE, participantType);
    }

    @Override
    public void deleteParticipantsByBaseEventId(final UUID baseEventId) {
        baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNotAndParticipantType(baseEventId, EntityStatus.DELETED, participantType)
                .forEach(participant -> saveItWithNewStatus(participant, EntityStatus.DELETED));
    }

    @Override
    public void deactivateParticipantsByBaseEventId(final UUID baseEventId) {
        getParticipantsByBaseEventId(baseEventId)
                .forEach(participant -> saveItWithNewStatus(participant, EntityStatus.INACTIVE));
    }

    @Override
    public void activateParticipantsByBaseEventId(final UUID baseEventId) {
        baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(baseEventId, EntityStatus.INACTIVE, participantType)
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
