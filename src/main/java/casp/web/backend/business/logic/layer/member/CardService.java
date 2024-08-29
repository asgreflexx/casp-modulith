package casp.web.backend.business.logic.layer.member;


import casp.web.backend.data.access.layer.documents.member.Card;

import java.util.Set;
import java.util.UUID;

/**
 * Interface CardService
 *
 * @author michaelsvoboda
 */
public interface CardService {

    Card saveCard(Card card);

    Set<Card> getCardsByMemberId(UUID memberId);

    void deleteCardById(UUID id);

    Card getCardById(UUID id);

    void deleteCardsByMemberId(UUID memberId);

    void deactivateCardsByMemberId(UUID memberId);

    void activateCardsByMemberId(UUID memberId);
}
