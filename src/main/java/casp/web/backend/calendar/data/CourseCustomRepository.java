package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseCustomRepository extends BaseEventCustomRepository<Course> {
    Page<Course> findAllByYear(int year, Pageable pageable);

    Page<Course> findAllBySpace(Space space, Pageable pageable);
}
