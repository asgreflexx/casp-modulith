package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static casp.web.backend.calendar.presentation.ExamReadMapper.EXAM_READ_MAPPER;
import static casp.web.backend.calendar.presentation.ExamWriteMapper.EXAM_WRITE_MAPPER;

@RequiredArgsConstructor
@RestController
@RequestMapping("exam")
@Validated
class ExamRestController {
    private final ExamService examService;

    @PostMapping
    ResponseEntity<Void> save(@RequestBody @Valid ExamWrite examWrite) {
        examService.save(EXAM_WRITE_MAPPER.toSource(examWrite));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        examService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("participants/{dogHasHandlerId}")
    ResponseEntity<Page<ExamRead>> getExamsByDogHasHandlerId(@PathVariable UUID dogHasHandlerId, @ParameterObject Pageable pageable) {
        var examDtoPage = examService.getExamsByDogHasHandlerId(dogHasHandlerId, pageable);
        return ResponseEntity.ok(EXAM_READ_MAPPER.toTargetPage(examDtoPage));
    }

    @GetMapping("{id}")
    ResponseEntity<ExamRead> getOneById(@PathVariable UUID id) {
        var examDto = examService.getOneById(id);
        return ResponseEntity.ok(EXAM_READ_MAPPER.toTarget(examDto));
    }
}
