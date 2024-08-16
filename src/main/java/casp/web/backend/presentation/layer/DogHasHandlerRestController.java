package casp.web.backend.presentation.layer;

import casp.web.backend.business.logic.layer.interfaces.IDogHasHandlerService;
import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.entities.DogHasHandler;
import casp.web.backend.data.access.layer.entities.Member;
import jakarta.validation.constraints.NotNull;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/doghashandler")
@Validated
public class DogHasHandlerRestController {


    private final IDogHasHandlerService iDogHasHandlerService;

    @Autowired
    DogHasHandlerRestController(IDogHasHandlerService iDogHasHandlerService) {
        this.iDogHasHandlerService = iDogHasHandlerService;
    }


    @GetMapping({"/{id}"})
    public ResponseEntity<DogHasHandler> getDataById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(iDogHasHandlerService.getDogHasHandlerById(id));
    }

    @GetMapping({"/getHandlersByDog/{id}"})
    public ResponseEntity<List<DogHasHandler>> getDogHasHandlerByDogId(@PathVariable("id") UUID dogId) {
        return ResponseEntity.ok(iDogHasHandlerService.getHandlerByDogId(dogId));
    }

    @GetMapping({"/getHandlersByMember/{id}"})
    public ResponseEntity<List<DogHasHandler>> getDogHasHandlerByMemberId(@PathVariable("id") UUID memberId) {
        return ResponseEntity.ok(iDogHasHandlerService.getHandlerByMemberId(memberId));
    }


    @GetMapping({"/getByMember/{id}"})
    public ResponseEntity<List<Dog>> getDataByMember(@PathVariable("id") UUID memberId) {
        return ResponseEntity.ok(iDogHasHandlerService.getDogsByMemberId(memberId));
    }

    @GetMapping({"/getByDog/{id}"})
    public ResponseEntity<List<Member>> getDataByDog(@PathVariable("id") UUID dogId) {
        return ResponseEntity.ok(iDogHasHandlerService.getMembersByDogId(dogId));
    }

    @PostMapping()
    //FIXME: https://unleashit-io.atlassian.net/browse/CASP-262
    @SuppressWarnings("java:S4684")
    public ResponseEntity<DogHasHandler> saveDogHasHandler(@RequestBody DogHasHandler dogHasHandler) {
        return ResponseEntity.ok(iDogHasHandlerService.saveDogHasHandler(dogHasHandler));
    }

    @DeleteMapping({"/deleteByDog/{id}"})
    public ResponseEntity<String> deleteDataByDog(@PathVariable("id") UUID dogId) {
        iDogHasHandlerService.deleteDogHasHandlerByDogId(dogId);
        return ResponseEntity.ok("Handler was deleted.");
    }

    @DeleteMapping({"/deleteByMember/{id}"})
    public ResponseEntity<String> deleteDataByMember(@PathVariable("id") UUID memberId) {
        iDogHasHandlerService.deleteDogHasHandlerByMemberId(memberId);
        return ResponseEntity.ok("Handler was deleted.");
    }

    @GetMapping("/searchDogHasHandlerByName")
    public ResponseEntity<List<DogHasHandler>> searchDogHasHandlerByFirstNameOrLastNameOrDogName(@RequestParam(required = false, defaultValue = "") String name) {
        return ResponseEntity.ok(iDogHasHandlerService.searchDogHasHandlerByFirstNameOrLastNameOrDogName(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DogHasHandlerDto>> getAllDogHasHandler() {
        return ResponseEntity.ok(iDogHasHandlerService.getAllDogHasHandler());
    }

    @GetMapping("/getHandlersByIds")
    public ResponseEntity<List<DogHasHandlerDto>> getDogHasHandlersByHandlerIds(@RequestParam List<UUID> dogHasHandlerIds) {
        return ResponseEntity.ok(iDogHasHandlerService.getDogHasHandlersByIds(dogHasHandlerIds));
    }

    @GetMapping("/getDogHasHandlerIdsByMemberId/{id}")
    public Set<UUID> getDogHasHandlerIdsByMemberId(@PathVariable @NotNull UUID id) {
        return iDogHasHandlerService.getDogHasHandlerIdsByMemberId(id);
    }

    @GetMapping("/getDogHasHandlerIdsByDogId/{id}")
    public Set<UUID> getDogHasHandlerIdsByDogId(@PathVariable @NotNull UUID id) {
        return iDogHasHandlerService.getDogHasHandlerIdsByDogId(id);
    }

    @GetMapping("/get-emails-by-ids")
    public Set<String> getMembersEmailByIds(@RequestParam @Size(min = 1) Set<UUID> ids) {
        return iDogHasHandlerService.getMembersEmailByIds(ids);
    }
}
