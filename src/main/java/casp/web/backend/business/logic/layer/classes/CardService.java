package casp.web.backend.business.logic.layer.classes;


import casp.web.backend.business.logic.layer.interfaces.ICardService;
import casp.web.backend.data.access.layer.entities.Card;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.ICardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

/**
 * Card Service
 *
 * @author michaelsvoboda
 */
@Service
public class CardService implements ICardService {

    private final ICardRepository iCardRepository;

    @Autowired
    CardService(ICardRepository iCardRepository) {
        this.iCardRepository = iCardRepository;
    }

    @Override
    public Card saveCard(Card card) {
        if (card.getId() == null) {
            card.setId(UUID.randomUUID());
        }
        return iCardRepository.save(card);
    }

    @Override
    public Collection<Card> getCardsByMemberId(UUID id) {
        //entity status is hard coded because it is not needed at the moment!
        return iCardRepository.findAllByMemberIdAndEntityStatus(id, EntityStatus.ACTIVE);
    }

    @Override
    public void deleteCardById(UUID id) throws Exception {
        Card card = iCardRepository.findById(id).orElseThrow(() -> new Exception("Card not found."));
        card.setEntityStatus(EntityStatus.DELETED);
        this.saveCard(card);
    }

    @Override
    public Card getCardById(UUID id) throws Exception {
        return iCardRepository.findById(id).orElseThrow(() -> new Exception("Card not found."));
    }
}
