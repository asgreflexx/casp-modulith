package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogService;
import casp.web.backend.presentation.layer.dtos.dog.DogDto;
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

import static casp.web.backend.presentation.layer.dtos.dog.DogMapper.DOG_MAPPER;

@RestController
@RequestMapping("/dog")
@Validated
class DogRestController {

    private final DogService dogService;

    @Autowired
    DogRestController(final DogService dogService) {
        this.dogService = dogService;
    }

    @GetMapping("/{id}")
    ResponseEntity<DogDto> getDogById(final @PathVariable UUID id) {
        return ResponseEntity.ok(DOG_MAPPER.toDto(dogService.getDogById(id)));
    }

    @GetMapping("/by-chip-number-or-dog-name-or-owner-name")
    @GetByOwnerCustomValidation(chipNumberParameter = "chipNumber", nameParameter = "name", ownerNameParameter = "ownerName")
    ResponseEntity<List<DogDto>> getDogsByOwner(final @RequestParam(required = false) String chipNumber,
                                                final @RequestParam(required = false) String name,
                                                final @RequestParam(required = false) String ownerName) {
        var dogList = dogService.getDogsByOwnerNameAndDogsNameOrChipNumber(chipNumber, name, ownerName);
        return ResponseEntity.ok(DOG_MAPPER.toDtoList(dogList));
    }

    @GetMapping()
    ResponseEntity<Page<DogDto>> getDogs(final @ParameterObject Pageable pageable) {
        var dogPage = dogService.getDogs(pageable);
        return ResponseEntity.ok(DOG_MAPPER.toDtoPage(dogPage));
    }

    @PostMapping()
    ResponseEntity<DogDto> postData(final @RequestBody @Valid DogDto dogDto) {
        var saveDog = dogService.saveDog(DOG_MAPPER.toDocument(dogDto));
        return ResponseEntity.ok(DOG_MAPPER.toDto(saveDog));
    }

    @DeleteMapping("/{id}")
    void deleteDataById(final @PathVariable UUID id) {
        dogService.deleteDogById(id);
    }
}
