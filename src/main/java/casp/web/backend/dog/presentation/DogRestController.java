package casp.web.backend.dog.presentation;

import casp.web.backend.dog.DogService;
import casp.web.backend.dog.EuropeNetTasks;
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

import java.util.UUID;

import static casp.web.backend.dog.presentation.DogReadMapper.READ_MAPPER;
import static casp.web.backend.dog.presentation.DogWriteMapper.WRITE_MAPPER;

@RestController
@RequestMapping("dog")
@Validated
class DogRestController {

    private final DogService dogService;
    private final EuropeNetTasks europeNetTasks;

    @Autowired
    DogRestController(DogService dogService, EuropeNetTasks europeNetTasks) {
        this.dogService = dogService;
        this.europeNetTasks = europeNetTasks;
    }

    @GetMapping("{id}")
    ResponseEntity<DogRead> getDogById(@PathVariable UUID id) {
        var dogDto = dogService.getDogById(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(dogDto));
    }

    @GetMapping
    ResponseEntity<Page<DogRead>> getDogs(@RequestParam(required = false, defaultValue = "") String value,
                                          @ParameterObject Pageable pageable) {
        var dogDtoPage = dogService.getDogs(value, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogDtoPage));
    }

    @PostMapping
    ResponseEntity<DogRead> saveDog(@RequestBody @Valid DogWrite dogWrite) {
        var dogDto = dogService.saveDog(WRITE_MAPPER.toSource(dogWrite));
        return ResponseEntity.ok(READ_MAPPER.toTarget(dogDto));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteDogById(@PathVariable UUID id) {
        dogService.deleteDogById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("register")
    ResponseEntity<Page<DogRead>> register(@ParameterObject Pageable pageable) {
        var dogDtoPage = europeNetTasks.registerDogsManually(pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogDtoPage));
    }

    @GetMapping("by-not-member-id/{memberId}")
    ResponseEntity<Page<DogRead>> getDogsByNotMemberId(@PathVariable UUID memberId, @RequestParam(required = false) String name, @ParameterObject Pageable pageable) {
        var dogDtoPage = dogService.getDogsByNotMemberId(memberId, name, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogDtoPage));
    }
}
