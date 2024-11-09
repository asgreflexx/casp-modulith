package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.reference.DogHasHandlerReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CourseCustomRepository extends BaseEventCustomRepository<Course> {
    Page<Course> findAllByYear(int year, Pageable pageable);

    Set<Course> findAllByDogHasHandlers(Set<DogHasHandlerReference> dogHasHandlers);
}
