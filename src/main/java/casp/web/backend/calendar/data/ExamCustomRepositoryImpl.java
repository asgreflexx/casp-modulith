package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.enums.EntityStatus;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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

    @Override
    public Stream<Exam> findAllBetweenFromAndToOrMemberId(LocalDateTime from, LocalDateTime to, @Nullable UUID memberId) {
        var criteria = createTimeRangeCriteria(from, to);
        if (memberId != null) {
            criteria = criteria.and(EXAM.member.id.eq(memberId)
                    .or(EXAM.participants.any().in(findDogHasHandlersAndMapToExamParticipants(memberId))));
        }
        return query()
                .where(criteria)
                .stream();
    }

    private List<ExamParticipant> findDogHasHandlersAndMapToExamParticipants(UUID memberId) {
        return findDogHasHandlersByMemberId(memberId)
                .map(ExamParticipant::new)
                .toList();
    }
}
