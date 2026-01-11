package casp.web.backend.calendar.data;

import casp.web.backend.calendar.CoursesFeesStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseCustomRepository extends BaseEventCustomRepository<Course> {
    Page<Course> findAllByYear(int year, Pageable pageable);

    Page<Course> findAllBySpaceId(UUID spaceId, Pageable pageable);

    CoursesFeesStatsDto getCoursesFeesStats();
}
