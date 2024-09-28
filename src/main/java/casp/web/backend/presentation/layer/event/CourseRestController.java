package casp.web.backend.presentation.layer.event;

import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import casp.web.backend.presentation.layer.event.facades.CourseFacade;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
