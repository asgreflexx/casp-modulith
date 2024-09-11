package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.documents.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
class EventParticipantServiceImpl extends BaseParticipantServiceImpl<EventParticipant, Event> implements EventParticipantService {

    @Autowired
    EventParticipantServiceImpl(final BaseParticipantRepository baseParticipantRepository) {
        super(baseParticipantRepository, EventParticipant.PARTICIPANT_TYPE);
    }
}
