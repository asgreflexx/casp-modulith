package casp.web.backend.presentation.layer;

import casp.web.backend.business.logic.layer.interfaces.ICardService;
import casp.web.backend.data.access.layer.entities.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


/**
 * Card RestController
 *
 * @author michaelsvoboda
 */
@RestController
@RequestMapping("/card")
public class CardRestController {

    private final ICardService cardService;

    @Autowired
    CardRestController(final ICardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping()
    public ResponseEntity<?> saveCard(@RequestBody Card card) {

        try {
            return ResponseEntity.ok(cardService.saveCard(card));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping({"/member_id/{id}"})
    public ResponseEntity<?> getCardsByMemberId(@PathVariable("id") UUID id) {

        try {
            return ResponseEntity.ok(cardService.getCardsByMemberId(id));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<?> deleteCardById(@PathVariable("id") UUID id) {

        try {
            cardService.deleteCardById(id);
            return ResponseEntity.ok("Card was successfully deleted!");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED); // The http status can be changed for another 4XX
        }
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<?> getCardById(@PathVariable("id") UUID id) {

        try {
            return ResponseEntity.ok(cardService.getCardById(id));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }
}
