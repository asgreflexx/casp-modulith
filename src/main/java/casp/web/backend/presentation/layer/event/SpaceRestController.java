package casp.web.backend.presentation.layer.event;

import casp.web.backend.presentation.layer.dtos.event.participants.SpaceReadDto;
import casp.web.backend.presentation.layer.event.facades.SpaceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/space")
@Validated
class SpaceRestController {
    private final SpaceFacade spaceFacade;

    @Autowired
    SpaceRestController(final SpaceFacade spaceFacade) {
        this.spaceFacade = spaceFacade;
    }

    @GetMapping("/by-member-id/{memberId}")
    ResponseEntity<Set<SpaceReadDto>> getSpacesByMemberId(final @PathVariable UUID memberId) {
        return ResponseEntity.ok(spaceFacade.getSpacesByMemberId(memberId));
    }

    @GetMapping("/by-dog-id/{dogId}")
    ResponseEntity<Set<SpaceReadDto>> getSpacesByDogId(final @PathVariable UUID dogId) {
        return ResponseEntity.ok(spaceFacade.getSpacesByDogId(dogId));
    }
}
