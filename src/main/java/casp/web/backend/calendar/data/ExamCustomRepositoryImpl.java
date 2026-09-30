package casp.web.backend.calendar.data;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
class ExamCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Exam> implements ExamCustomRepository {
    private static final QExam EXAM = QExam.exam;

    @Autowired
    ExamCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    public Page<Exam> findAllByParticipantId(UUID participantId, Pageable pageable) {
        if (findActiveDogHandlerReferenceById(participantId).isEmpty()) {
            return Page.empty();
        }

        var participantCriteria = EXAM.participants.any().dogHasHandler.id.eq(participantId);
        return query()
                .where(participantCriteria, EXAM.entityStatus.eq(EntityStatus.ACTIVE))
                .fetchPage(pageable);
    }
}
