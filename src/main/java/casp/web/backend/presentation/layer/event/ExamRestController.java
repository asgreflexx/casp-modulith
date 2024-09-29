package casp.web.backend.presentation.layer.event;

import casp.web.backend.presentation.layer.dtos.event.types.ExamDto;
import casp.web.backend.presentation.layer.event.facades.ExamFacade;
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
@RequestMapping("/exam")
@Validated
class ExamRestController {
    private final ExamFacade examFacade;

    @Autowired
    ExamRestController(final ExamFacade examFacade) {
        this.examFacade = examFacade;
    }

    @PostMapping
    ResponseEntity<Void> save(final @RequestBody @Valid ExamDto examDto) {
        examFacade.save(examDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ExamDto> getOneById(final @PathVariable UUID id) {
        return ResponseEntity.ok(examFacade.getOneById(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteById(final @PathVariable UUID id) {
        examFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    ResponseEntity<Page<ExamDto>> getAllByYear(final @RequestParam @Positive int year, final @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(examFacade.getAllByYear(year, pageable));
    }
}
