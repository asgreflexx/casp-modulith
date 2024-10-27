package casp.web.backend.data.access.layer.event.types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
class ExamCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Exam> implements ExamCustomRepository {

    @Autowired
    ExamCustomRepositoryImpl(final MongoOperations mongoOperations) {
        super(Exam.class, mongoOperations);
    }
}
