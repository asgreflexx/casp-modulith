package casp.web.backend.calendar.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
class EventCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Event> implements EventCustomRepository {

    @Autowired
    EventCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }
}
