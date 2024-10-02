package casp.web.backend.business.logic.layer.member;


import casp.web.backend.data.access.layer.member.Card;

import java.util.Set;
import java.util.UUID;

public interface CardService {

    /**
     * It sets the member, before saving it.
     *
     * @param card instance of Card
     * @return saved instance of Card
     */
    Card saveCard(Card card);

    Set<Card> getCardsByMemberId(UUID memberId);

    void deleteCardById(UUID id);

    Card getCardById(UUID id);

    /**
     * Set all cards with the given memberId to deleted status.
     * This is used when a member is deleted.
     * @param memberId the id of the member
     */
    void deleteCardsByMemberId(UUID memberId);

    /**
     * Set all cards with the given memberId to inactive status.
     * This is used when a member is deactivated.
     * @param memberId the id of the member
     */
    void deactivateCardsByMemberId(UUID memberId);

    /**
     * Set all cards with the given memberId to active status.
     * This is used when a member is activated.
     * @param memberId the id of the member
     */
    void activateCardsByMemberId(UUID memberId);
}
