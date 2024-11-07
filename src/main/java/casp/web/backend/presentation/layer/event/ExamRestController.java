package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

import static casp.web.backend.presentation.layer.event.ExamReadMapper.EXAM_READ_MAPPER;
import static casp.web.backend.presentation.layer.event.ExamWriteMapper.EXAM_WRITE_MAPPER;

@RestController
@RequestMapping("exam")
@Validated
class ExamRestController {
    private final ExamService examService;

    @Autowired
    ExamRestController(ExamService examService) {
        this.examService = examService;
    }

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

    @GetMapping("{examId}/calendar-entry/{calendarEntryId}")
    ResponseEntity<ExamRead> getCalendarEntry(@PathVariable UUID examId, @PathVariable UUID calendarEntryId) {
        var examDto = examService.getOneByIdAndCalendarEntryId(examId, calendarEntryId);
        return ResponseEntity.ok(EXAM_READ_MAPPER.toTarget(examDto));
    }

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    @PostMapping("migrate-data")
    ResponseEntity<Void> migrateDataToV2() {
        examService.migrateDataToV2();
        return ResponseEntity.noContent().build();
    }
}
