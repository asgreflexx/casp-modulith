package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.ExamParticipant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
class ExamCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Exam, ExamParticipant> implements ExamCustomRepository {

    @Autowired
    ExamCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }
}
