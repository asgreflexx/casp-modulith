package casp.web.backend.presentation.layer;

import casp.web.backend.business.logic.layer.interfaces.IDogService;
import casp.web.backend.data.access.layer.entities.Dog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/dog")
public class DogRestController {

    // try und catch immer nach gleichem Schema
    // wichtig sind vor allem die korrekten Mappings
    private final IDogService dogService;

    @Autowired
    DogRestController(final IDogService dogService) {
        this.dogService = dogService;
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<?> getDogById(@PathVariable("id") UUID id) {

        try {
            return ResponseEntity.ok(dogService.getDogById(id));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping({"/getByOwner"})
    public ResponseEntity<?> getDogsByOwner(@RequestParam String ownerName, @RequestParam String name, @RequestParam String chipNumber) {

        try {
            return ResponseEntity.ok(dogService.getDogsByOwner(ownerName, name, chipNumber));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping({"/active"})
    public ResponseEntity<?> getDogs() {

        try {
            return ResponseEntity.ok(dogService.getDogs());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    public ResponseEntity<?> postData(@RequestBody Dog dog) {

        try {
            return ResponseEntity.ok(dogService.saveDog(dog));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<?> deleteDataById(@PathVariable("id") UUID id) {

        try {
            dogService.deleteDogById(id);
            return ResponseEntity.ok("Dog was deleted.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }
}

