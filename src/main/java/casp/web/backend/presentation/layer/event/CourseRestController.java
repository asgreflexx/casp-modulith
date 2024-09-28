package casp.web.backend.presentation.layer.event;

import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import casp.web.backend.presentation.layer.event.facades.CourseFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/course")
@Validated
class CourseRestController {
    private final CourseFacade courseFacade;

    @Autowired
    CourseRestController(final CourseFacade courseFacade) {
        this.courseFacade = courseFacade;
    }

    @PostMapping
    ResponseEntity<Void> save(final @RequestBody @Valid CourseDto courseDto) {
        courseFacade.save(courseDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    ResponseEntity<CourseDto> getOneById(final @PathVariable UUID id) {
        return ResponseEntity.ok(courseFacade.getOneById(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteById(final @PathVariable UUID id) {
        courseFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    ResponseEntity<Page<CourseDto>> getAllByYear(final @RequestParam @Positive int year, final @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(courseFacade.getAllByYear(year, pageable));
    }
}
