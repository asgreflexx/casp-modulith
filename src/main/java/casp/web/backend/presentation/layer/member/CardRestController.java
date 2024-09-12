package casp.web.backend.presentation.layer.member;

import casp.web.backend.business.logic.layer.member.CardService;
import casp.web.backend.presentation.layer.dtos.member.CardDto;
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

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.member.CardMapper.CARD_MAPPER;


/**
 * Card RestController
 *
 * @author michaelsvoboda
 */
@RestController
@RequestMapping("/card")
@Validated
class CardRestController {

    private final CardService cardService;

    @Autowired
    CardRestController(final CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    ResponseEntity<CardDto> saveCard(final @RequestBody @Valid CardDto cardDto) {
        var card = cardService.saveCard(CARD_MAPPER.toDocument(cardDto));
        return ResponseEntity.ok(CARD_MAPPER.toDto(card));
    }

    @GetMapping("/by-member-id/{memberId}")
    ResponseEntity<Set<CardDto>> getCardsByMemberId(final @PathVariable UUID memberId) {
        var cardSet = cardService.getCardsByMemberId(memberId);
        return ResponseEntity.ok(CARD_MAPPER.toDtoSet(cardSet));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCardById(final @PathVariable UUID id) {
        cardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    ResponseEntity<CardDto> getCardById(final @PathVariable UUID id) {
        var card = cardService.getCardById(id);
        return ResponseEntity.ok(CARD_MAPPER.toDto(card));
    }
}
