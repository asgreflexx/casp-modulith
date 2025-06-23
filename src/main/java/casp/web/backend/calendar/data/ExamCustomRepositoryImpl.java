package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.enums.EntityStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
class ExamCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Exam> implements ExamCustomRepository {
    private static final QExam EXAM = QExam.exam;

    @Autowired
    ExamCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    public Page<Exam> findAllByParticipant(ExamParticipant participant, Pageable pageable) {
        return query()
                .where(EXAM.participants.contains(participant), EXAM.entityStatus.eq(EntityStatus.ACTIVE))
                .fetchPage(pageable);
    }
}
