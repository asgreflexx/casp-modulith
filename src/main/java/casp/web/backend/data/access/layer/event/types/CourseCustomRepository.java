package casp.web.backend.data.access.layer.event.types;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseCustomRepository extends BaseEventCustomRepository<Course> {
    Page<Course> findAllByYear(int year, final Pageable pageable);
}
