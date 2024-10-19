package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends BaseRepository<Course>, CourseCustomRepository {
    Optional<Course> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);
}
