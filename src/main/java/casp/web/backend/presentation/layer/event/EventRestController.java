package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.EventService;
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

import static casp.web.backend.presentation.layer.event.EventReadMapper.EVENT_READ_MAPPER;
import static casp.web.backend.presentation.layer.event.EventWriteMapper.EVENT_WRITE_MAPPER;

@RestController
@RequestMapping("event")
@Validated
class EventRestController {
    private final EventService eventService;

    @Autowired
    EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

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

    @GetMapping("{eventId}/calendar-entry/{calendarEntryId}")
    ResponseEntity<EventRead> getCalendarEntry(@PathVariable UUID eventId, @PathVariable UUID calendarEntryId) {
        var eventDto = eventService.getOneByIdAndCalendarEntryId(eventId, calendarEntryId);
        return ResponseEntity.ok(EVENT_READ_MAPPER.toTarget(eventDto));
    }

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    @PostMapping("migrate-data")
    ResponseEntity<Void> migrateDataToV2() {
        eventService.migrateDataToV2();
        return ResponseEntity.noContent().build();
    }
}
