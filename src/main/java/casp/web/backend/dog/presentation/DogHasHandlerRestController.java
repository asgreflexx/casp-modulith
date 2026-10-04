package casp.web.backend.dog.presentation;

import casp.web.backend.dog.DogHasHandlerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.dog.presentation.DogHasHandlerReadMapper.READ_MAPPER;
import static casp.web.backend.dog.presentation.DogHasHandlerWriteMapper.WRITE_MAPPER;

@RequiredArgsConstructor
@RestController
@RequestMapping("dog-has-handler")
@Validated
class DogHasHandlerRestController {

    private final DogHasHandlerService dogHasHandlerService;

    @GetMapping("{id}")
    ResponseEntity<DogHasHandlerRead> getDogHasHandlerById(@PathVariable UUID id) {
        var dogHasHandlerDto = dogHasHandlerService.getDogHasHandlerById(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @PostMapping
    ResponseEntity<DogHasHandlerRead> saveDogHasHandler(@RequestBody @Valid DogHasHandlerWrite dogHasHandlerWrite) {
        var dogHasHandlerDto = WRITE_MAPPER.toSource(dogHasHandlerWrite);
        dogHasHandlerDto = dogHasHandlerService.saveDogHasHandler(dogHasHandlerDto);
        return ResponseEntity.ok(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteDogHasHandlerById(@PathVariable UUID id) {
        dogHasHandlerService.deleteDogHasHandlerById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("search-by-value")
    ResponseEntity<Page<DogHasHandlerRead>> searchByValue(@RequestParam(required = false, defaultValue = "") String value,
                                                          @ParameterObject Pageable pageable) {
        var dogHasHandlerDtoPage = dogHasHandlerService.searchByValue(value, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogHasHandlerDtoPage));
    }

    @GetMapping
    ResponseEntity<Page<DogHasHandlerRead>> getAllDogHasHandlers(@ParameterObject Pageable pageable) {
        var dogHasHandlerDtoPage = dogHasHandlerService.getAllDogHasHandlers(pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogHasHandlerDtoPage));
    }

    @GetMapping("by-ids")
    ResponseEntity<Set<DogHasHandlerRead>> getDogHasHandlersByHandlerIds(@RequestParam @Size(min = 1) Set<UUID> ids) {
        var dogHasHandlerDtoSet = dogHasHandlerService.getDogHasHandlersByIds(ids);
        return ResponseEntity.ok(READ_MAPPER.toTargetSet(dogHasHandlerDtoSet));
    }

    @GetMapping("emails-by-ids")
    ResponseEntity<Set<String>> getMembersEmailByIds(@RequestParam @Size(min = 1) Set<UUID> ids) {
        return ResponseEntity.ok(dogHasHandlerService.getEmailsByDogHasHandlersIds(ids));
    }

    @GetMapping("by-member-id/{memberId}")
    ResponseEntity<Set<DogHasHandlerRead>> getDogHasHandlerByMemberId(@PathVariable UUID memberId) {
        return ResponseEntity.ok(READ_MAPPER.toTargetSet(dogHasHandlerService.getDogHasHandlerByMemberId(memberId)));
    }

    @GetMapping("by-dog-id/{dogId}")
    ResponseEntity<Set<DogHasHandlerRead>> getDogHasHandlerByDogId(@PathVariable UUID dogId) {
        return ResponseEntity.ok(READ_MAPPER.toTargetSet(dogHasHandlerService.getDogHasHandlerByDogId(dogId)));
    }

    @PostMapping("correct-entity-status")
    ResponseEntity<Void> correctEntityStatus() {
        dogHasHandlerService.correctEntityStatus();
        return ResponseEntity.noContent().build();
    }
}
