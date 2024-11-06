package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.CourseService;
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

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.event.CourseReadMapper.COURSE_READ_MAPPER;
import static casp.web.backend.presentation.layer.event.CourseWriteMapper.COURSE_WRITE_MAPPER;

@RestController
@RequestMapping("course")
@Validated
class CourseRestController {
    private final CourseService courseService;

    @Autowired
    CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    ResponseEntity<Void> save(@RequestBody @Valid CourseWrite courseWrite) {
        courseService.save(COURSE_WRITE_MAPPER.toSource(courseWrite));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    ResponseEntity<CourseRead> getOneById(@PathVariable UUID id) {
        var courseDto = courseService.getOneById(id);
        return ResponseEntity.ok(COURSE_READ_MAPPER.toTarget(courseDto));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        courseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    ResponseEntity<Page<CourseRead>> getAllByYear(@RequestParam @Positive int year, @ParameterObject Pageable pageable) {
        var courseDtoPage = courseService.getAllByYear(year, pageable);
        return ResponseEntity.ok(COURSE_READ_MAPPER.toTargetPage(courseDtoPage));
    }

    @GetMapping("emails/{id}")
    ResponseEntity<Set<String>> getSpacesEmail(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getEmailsByCourseId(id));
    }
}
