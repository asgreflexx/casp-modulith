package casp.web.backend.business.logic.layer.member;


import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.member.Card;
import casp.web.backend.data.access.layer.repositories.CardRepository;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

/**
 * Card Service
 *
 * @author michaelsvoboda
 */
@Service
class CardServiceImpl implements CardService {
    private static final Logger LOG = LoggerFactory.getLogger(CardServiceImpl.class);

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    @Autowired
    CardServiceImpl(final CardRepository cardRepository, final MemberRepository memberRepository) {
        this.cardRepository = cardRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Card saveCard(final Card card) {
        var member = memberRepository.findByIdAndEntityStatusCustom(card.getMemberId(), EntityStatus.ACTIVE);
        card.setMember(member);
        return cardRepository.save(card);
    }

    @Override
    public Set<Card> getCardsByMemberId(final UUID memberId) {
        return cardRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE);
    }

    @Override
    public void deleteCardById(final UUID id) {
        cardRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
                .ifPresent(card -> saveItWithStatus(card, EntityStatus.DELETED));
    }

    @Override
    public Card getCardById(final UUID id) {
        return cardRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "Card with id %s not found or it isn't %s.".formatted(id, EntityStatus.ACTIVE);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    @Override
    public void deleteCardsByMemberId(final UUID memberId) {
        cardRepository.findAllByMemberIdAndEntityStatusNot(memberId, EntityStatus.DELETED)
                .forEach(card -> saveItWithStatus(card, EntityStatus.DELETED));
    }

    @Override
    public void deactivateCardsByMemberId(final UUID memberId) {
        cardRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE)
                .forEach(card -> saveItWithStatus(card, EntityStatus.INACTIVE));
    }

    @Override
    public void activateCardsByMemberId(final UUID memberId) {
        cardRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE)
                .forEach(card -> saveItWithStatus(card, EntityStatus.ACTIVE));
    }

    private void saveItWithStatus(final Card card, final EntityStatus deleted) {
        card.setEntityStatus(deleted);
        cardRepository.save(card);
    }
}
