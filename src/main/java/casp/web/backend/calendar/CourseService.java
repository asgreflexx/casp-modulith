package casp.web.backend.calendar;


import casp.web.backend.common.reference.DogHasHandlerReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface CourseService extends BaseEventService<CourseDto> {

    Page<CourseDto> getAllByYear(int year, Pageable pageable);

    Set<String> getEmailsByCourseId(UUID id);

    void updateSpace(UUID courseId, SpaceDto spaceDto);

    void removeSpace(UUID courseId, UUID spaceId);

    Set<SpaceDto> getSpacesByDogHasHandlers(Set<DogHasHandlerReference> dogHasHandlerSet);
}
