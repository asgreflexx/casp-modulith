package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.data.access.layer.event.participants.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface CourseService {

    void save(CourseDto courseDto);

    CourseDto getOneById(UUID id);

    Page<CourseDto> getAllByYear(int year, Pageable pageable);

    void deleteById(UUID id);

    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    void migrateDataToV2();

    Set<String> getEmailsByCourseId(UUID id);

    void saveSpace(UUID courseId, Space space);

    void removeSpace(UUID courseId, UUID spaceId);
}
