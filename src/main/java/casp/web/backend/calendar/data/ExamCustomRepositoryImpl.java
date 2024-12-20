package casp.web.backend.calendar.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
class ExamCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Exam> implements ExamCustomRepository {

    @Autowired
    ExamCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }
}
