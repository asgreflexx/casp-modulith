package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.EventParticipant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
class EventCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Event, EventParticipant> implements EventCustomRepository {

    @Autowired
    EventCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

}
