package casp.web.backend.business.logic.layer.interfaces;

import casp.web.backend.data.access.layer.entities.Card;

import java.util.Collection;
import java.util.UUID;

/**
 * Interface CardService
 *
 * @author michaelsvoboda
 */
public interface ICardService {

    Card saveCard(Card card);

    Collection<Card> getCardsByMemberId(UUID id);

    void deleteCardById(UUID id) throws Exception;

    Card getCardById(UUID id) throws Exception;
}
