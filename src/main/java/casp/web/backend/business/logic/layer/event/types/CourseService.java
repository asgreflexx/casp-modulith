package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.data.access.layer.event.participants.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface CourseService extends BaseEventService<CourseDto> {

    Page<CourseDto> getAllByYear(int year, Pageable pageable);

    Set<String> getEmailsByCourseId(UUID id);

    void saveSpace(UUID courseId, Space space);

    void removeSpace(UUID courseId, UUID spaceId);
}
