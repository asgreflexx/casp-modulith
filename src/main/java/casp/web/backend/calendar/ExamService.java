package casp.web.backend.calendar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExamService extends BaseEventService<ExamDto> {
    Page<ExamDto> getExamsByDogHasHandlerId(UUID dogHasHandlerId, Pageable pageable);
}
