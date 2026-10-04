package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.CourseService;
import casp.web.backend.calendar.CoursesFeesStatsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.calendar.presentation.CourseReadMapper.COURSE_READ_MAPPER;
import static casp.web.backend.calendar.presentation.CourseWriteMapper.COURSE_WRITE_MAPPER;

@RequiredArgsConstructor
@RestController
@RequestMapping("course")
@Validated
class CourseRestController {
    private final CourseService courseService;


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

    @PatchMapping("{courseId}/spaces")
    ResponseEntity<CourseRead> updateSpaces(@PathVariable UUID courseId, @RequestHeader(value = HttpHeaders.IF_MATCH) long version, @RequestBody Set<@Valid SpaceWrite> spaces) {
        var spaceDtos = COURSE_WRITE_MAPPER.toSpaceDtos(spaces);
        var courseDto = courseService.updateSpaces(courseId, version, spaceDtos);
        return ResponseEntity.ok(COURSE_READ_MAPPER.toTarget(courseDto));
    }

    @GetMapping("space/{dogHasHandlerId}")
    public ResponseEntity<Page<CourseRead>> getCoursesByDogHasHandlerId(@PathVariable UUID dogHasHandlerId, @ParameterObject Pageable pageable) {
        var courseDtoPage = courseService.getCoursesByDogHasHandlerId(dogHasHandlerId, pageable);
        return ResponseEntity.ok(COURSE_READ_MAPPER.toTargetPage(courseDtoPage));
    }

    @GetMapping("courses-fees-stats")
    ResponseEntity<CoursesFeesStatsDto> getCoursesFeesStats() {
        return ResponseEntity.ok(courseService.getCoursesFeesStats());
    }
}
