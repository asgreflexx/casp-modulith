package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface CourseCustomRepository {
    Page<Course> findAllByYear(int year, final Pageable pageable);

    Set<Course> findAllByMemberIdAndNotDeleted(UUID memberId);

    Set<Course> findAllByMemberIdAndStatus(UUID memberId, EntityStatus status);
}
