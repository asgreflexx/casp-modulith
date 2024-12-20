package casp.web.backend.calendar.data;

import casp.web.backend.common.reference.DogHasHandlerReference;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.stream.Stream;

public interface CourseCustomRepository extends BaseEventCustomRepository<Course> {
    Page<Course> findAllByYear(int year, Pageable pageable);

    Stream<Course> findAllByDogHasHandlers(@NotEmpty Set<DogHasHandlerReference> dogHasHandlers);
}
