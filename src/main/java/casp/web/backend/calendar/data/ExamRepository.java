package casp.web.backend.calendar.data;

import casp.web.backend.common.base.BaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

@JaversSpringDataAuditable
public interface ExamRepository extends BaseRepository<Exam>, ExamCustomRepository {
}
