package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import static casp.web.backend.calendar.presentation.EventReadMapper.EVENT_READ_MAPPER;
import static casp.web.backend.calendar.presentation.EventWriteMapper.EVENT_WRITE_MAPPER;

@RequiredArgsConstructor
@RestController
@RequestMapping("event")
@Validated
class EventRestController {
    private final EventService eventService;

    @PostMapping
    ResponseEntity<Void> save(@RequestBody @Valid EventWrite eventWrite) {
        eventService.save(EVENT_WRITE_MAPPER.toSource(eventWrite));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    ResponseEntity<EventRead> getOneById(@PathVariable UUID id) {
        var eventDto = eventService.getOneById(id);
        return ResponseEntity.ok(EVENT_READ_MAPPER.toTarget(eventDto));
    }
}
