package casp.web.backend.calendar;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface CourseService extends BaseEventService<CourseDto> {

    Page<CourseDto> getAllByYear(int year, Pageable pageable);

    Set<String> getEmailsByCourseId(UUID id);

    void updateSpace(UUID courseId, SpaceDto spaceDto);

    void removeSpace(UUID courseId, UUID spaceId);

    Page<CourseDto> getCourseByDogHasHandlerId(UUID dogHasHandlerId, Pageable pageable);
}
