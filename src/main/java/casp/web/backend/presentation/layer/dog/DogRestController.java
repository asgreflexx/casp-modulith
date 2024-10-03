package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import casp.web.backend.business.logic.layer.dog.DogService;
import casp.web.backend.business.logic.layer.dog.EuropeNetTasks;
import casp.web.backend.presentation.layer.dtos.dog.DogDto;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.dog.DogMapper.DOG_MAPPER;

@RestController
@RequestMapping("/dog")
@Validated
class DogRestController {

    private final DogService dogService;
    private final DogHasHandlerService dogHasHandlerService;
    private final EuropeNetTasks europeNetTasks;

    @Autowired
    DogRestController(final DogService dogService, final DogHasHandlerService dogHasHandlerService, final EuropeNetTasks europeNetTasks) {
        this.dogService = dogService;
        this.dogHasHandlerService = dogHasHandlerService;
        this.europeNetTasks = europeNetTasks;
    }

    @GetMapping("/{id}")
    ResponseEntity<DogDto> getDogById(final @PathVariable UUID id) {
        var dogDto = DOG_MAPPER.toDto(dogService.getDogById(id));
        var dogHasHandlerSet = dogHasHandlerService.getDogHasHandlersByDogId(id);
        dogDto.setDogHasHandlerSet(DOG_HAS_HANDLER_MAPPER.toDtoSet(dogHasHandlerSet));
        return ResponseEntity.ok(dogDto);
    }

    @GetMapping("/by-chip-number-or-dog-name-or-owner-name")
    @GetByOwnerCustomValidation(chipNumberParameter = "chipNumber", nameParameter = "name", ownerNameParameter = "ownerName")
    ResponseEntity<List<DogDto>> getDogsByOwner(final @RequestParam(required = false) String chipNumber,
                                                final @RequestParam(required = false) String name,
                                                final @RequestParam(required = false) String ownerName) {
        var dogList = dogService.getDogsByOwnerNameAndDogsNameOrChipNumber(chipNumber, name, ownerName);
        return ResponseEntity.ok(DOG_MAPPER.toDtoList(dogList));
    }

    @GetMapping
    ResponseEntity<Page<DogDto>> getDogs(final @ParameterObject Pageable pageable) {
        var dogPage = dogService.getDogs(pageable);
        return ResponseEntity.ok(DOG_MAPPER.toDtoPage(dogPage));
    }

    @PostMapping
    ResponseEntity<DogDto> saveDog(final @RequestBody @Valid DogDto dogDto) {
        dogService.saveDog(DOG_MAPPER.toDocument(dogDto));
        return getDogById(dogDto.getId());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDogById(final @PathVariable UUID id) {
        dogService.deleteDogById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    ResponseEntity<Page<DogDto>> register(final @ParameterObject @Nullable Pageable pageable) {
        var dogPage = europeNetTasks.registerDogsManually(pageable);
        return ResponseEntity.ok(DOG_MAPPER.toDtoPage(dogPage));
    }
}
