package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import casp.web.backend.presentation.layer.dtos.dog.DogDto;
import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;
import casp.web.backend.presentation.layer.dtos.member.MemberDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
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

import static casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.dog.DogMapper.DOG_MAPPER;
import static casp.web.backend.presentation.layer.dtos.member.MemberMapper.MEMBER_MAPPER;

@RestController
@RequestMapping("/dog-has-handler")
@Validated
class DogHasHandlerRestController {

    private final DogHasHandlerService dogHasHandlerService;

    @Autowired
    DogHasHandlerRestController(final DogHasHandlerService dogHasHandlerService) {
        this.dogHasHandlerService = dogHasHandlerService;
    }

    @GetMapping("/{id}")
    ResponseEntity<DogHasHandlerDto> getDogHasHandlerById(final @PathVariable UUID id) {
        var dogHasHandler = dogHasHandlerService.getDogHasHandlerById(id);
        return ResponseEntity.ok(DOG_HAS_HANDLER_MAPPER.toDto(dogHasHandler));
    }

    @GetMapping("/by-dog-id/{dogId}")
    ResponseEntity<Set<DogHasHandlerDto>> getDogHasHandlerByDogId(final @PathVariable UUID dogId) {
        var dogHasHandlers = dogHasHandlerService.getDogHasHandlersByDogId(dogId);
        return ResponseEntity.ok(DOG_HAS_HANDLER_MAPPER.toDtoSet(dogHasHandlers));
    }

    @GetMapping("/by-member-id/{memberId}")
    ResponseEntity<Set<DogHasHandlerDto>> getDogHasHandlerByMemberId(final @PathVariable UUID memberId) {
        var dogHasHandlers = dogHasHandlerService.getDogHasHandlersByMemberId(memberId);
        return ResponseEntity.ok(DOG_HAS_HANDLER_MAPPER.toDtoSet(dogHasHandlers));
    }


    @GetMapping("/dogs-by-member-id/{memberId}")
    ResponseEntity<Set<DogDto>> getDogsByMemberId(final @PathVariable UUID memberId) {
        var dogSet = dogHasHandlerService.getDogsByMemberId(memberId);
        return ResponseEntity.ok(DOG_MAPPER.toDtoSet(dogSet));
    }

    @GetMapping("/members-by-dog-id/{dogId}")
    ResponseEntity<Set<MemberDto>> getMembersByDogId(final @PathVariable UUID dogId) {
        var members = dogHasHandlerService.getMembersByDogId(dogId);
        return ResponseEntity.ok(MEMBER_MAPPER.toDtoSet(members));
    }

    @PostMapping()
    ResponseEntity<DogHasHandlerDto> saveDogHasHandler(final @RequestBody @Valid DogHasHandlerDto dogHasHandlerDto) {
        var dogHasHandler = dogHasHandlerService.saveDogHasHandler(DOG_HAS_HANDLER_MAPPER.toDocument(dogHasHandlerDto));
        return ResponseEntity.ok(DOG_HAS_HANDLER_MAPPER.toDto(dogHasHandler));
    }

    @DeleteMapping("/by-dog-id/{dogId}")
    void deleteDogHasHandlersByDogId(final @PathVariable UUID dogId) {
        dogHasHandlerService.deleteDogHasHandlersByDogId(dogId);
    }

    @DeleteMapping("/by-member-id/{memberId}")
    void deleteDogHasHandlersByMemberId(final @PathVariable UUID memberId) {
        dogHasHandlerService.deleteDogHasHandlersByMemberId(memberId);
    }

    @GetMapping("/search-by-name")
    ResponseEntity<Set<DogHasHandlerDto>> searchByName(final @RequestParam(required = false, defaultValue = "") String name) {
        var dogHasHandlers = dogHasHandlerService.searchByName(name);
        return ResponseEntity.ok(DOG_HAS_HANDLER_MAPPER.toDtoSet(dogHasHandlers));
    }

    @GetMapping()
    ResponseEntity<Set<DogHasHandlerDto>> getAllDogHasHandler() {
        var dogHasHandlers = dogHasHandlerService.getAllDogHasHandler();
        return ResponseEntity.ok(DOG_HAS_HANDLER_MAPPER.toDtoSet(dogHasHandlers));
    }

    @GetMapping("/by-ids")
    ResponseEntity<Set<DogHasHandlerDto>> getDogHasHandlersByHandlerIds(final @RequestParam @Size(min = 1) Set<UUID> ids) {
        var dogHasHandlers = dogHasHandlerService.getDogHasHandlersByIds(ids);
        return ResponseEntity.ok(DOG_HAS_HANDLER_MAPPER.toDtoSet(dogHasHandlers));
    }

    @GetMapping("/dog-has-handler-ids-by-member-id/{memberId}")
    Set<UUID> getDogHasHandlerIdsByMemberId(final @PathVariable UUID memberId) {
        return dogHasHandlerService.getDogHasHandlerIdsByMemberId(memberId);
    }

    @GetMapping("/dog-has-handler-ids-by-dog-id/{dogId}")
    Set<UUID> getDogHasHandlerIdsByDogId(final @PathVariable UUID dogId) {
        return dogHasHandlerService.getDogHasHandlerIdsByDogId(dogId);
    }

    @GetMapping("/get-emails-by-ids")
    Set<String> getMembersEmailByIds(final @RequestParam @Size(min = 1) Set<UUID> ids) {
        return dogHasHandlerService.getMembersEmailByIds(ids);
    }
}
