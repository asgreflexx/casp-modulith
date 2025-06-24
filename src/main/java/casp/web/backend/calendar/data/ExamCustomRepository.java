package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.ExamParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExamCustomRepository extends BaseEventCustomRepository<Exam> {
    Page<Exam> findAllByParticipant(ExamParticipant participant, Pageable pageable);
}
