package casp.web.backend.business.logic.layer.events.participants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
class BaseParticipantObserverImpl implements BaseParticipantObserver {
    private final Set<BaseParticipantService<?, ?>> observers = new HashSet<>();

    @Autowired
    BaseParticipantObserverImpl(final CoTrainerService coTrainerService,
                                final EventParticipantService eventParticipant,
                                final ExamParticipantService examParticipantService,
                                final SpaceService spaceService) {
        observers.add(coTrainerService);
        observers.add(eventParticipant);
        observers.add(examParticipantService);
        observers.add(spaceService);
    }

    @Override
    public void deleteParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        observers.forEach(observer -> observer.deleteParticipantsByMemberOrHandlerId(memberOrHandlerId));
    }

    @Override
    public void deactivateParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        observers.forEach(observer -> observer.deactivateParticipantsByMemberOrHandlerId(memberOrHandlerId));
    }

    @Override
    public void activateParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        observers.forEach(observer -> observer.activateParticipantsByMemberOrHandlerId(memberOrHandlerId));
    }
}
