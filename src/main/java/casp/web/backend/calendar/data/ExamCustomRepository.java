package casp.web.backend.calendar.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExamCustomRepository extends BaseEventCustomRepository<Exam> {
    Page<Exam> findAllByParticipantId(UUID participantId, Pageable pageable);
}
