package casp.web.backend.business.logic.layer.member;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.member.Card;
import casp.web.backend.data.access.layer.member.CardRepository;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CardServiceImpl cardService;
    private Member member;
    private Card card;

    @BeforeEach
    void setUp() {
        member = new Member();
        card = new Card();
        card.setMemberId(member.getId());
    }

    @Test
    void saveCard() {
        when(memberRepository.findByIdAndEntityStatusCustom(member.getId(), EntityStatus.ACTIVE)).thenReturn(member);
        when(cardRepository.save(card)).thenReturn(card);

        var saveCard = cardService.saveCard(card);

        assertSame(member, saveCard.getMember());
        assertSame(card, saveCard);
    }

    @Test
    void getCardsByMemberId() {
        when(cardRepository.findAllByMemberIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Set.of(card));

        assertThat(cardService.getCardsByMemberId(member.getId())).containsExactly(card);
    }

    @Test
    void deleteCardById() {
        when(cardRepository.findByIdAndEntityStatusNot(card.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(card));

        cardService.deleteCardById(card.getId());

        assertSame(EntityStatus.DELETED, card.getEntityStatus());
    }

    @Test
    void deleteCardsByMemberId() {
        when(cardRepository.findAllByMemberIdAndEntityStatusNot(member.getId(), EntityStatus.DELETED)).thenReturn(Set.of(card));

        cardService.deleteCardsByMemberId(member.getId());

        assertSame(EntityStatus.DELETED, card.getEntityStatus());
    }

    @Test
    void deactivateCardsByMemberId() {
        when(cardRepository.findAllByMemberIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Set.of(card));

        cardService.deactivateCardsByMemberId(member.getId());

        assertSame(EntityStatus.INACTIVE, card.getEntityStatus());
    }

    @Test
    void activateCardsByMemberId() {
        when(cardRepository.findAllByMemberIdAndEntityStatus(member.getId(), EntityStatus.INACTIVE)).thenReturn(Set.of(card));

        cardService.activateCardsByMemberId(member.getId());

        assertSame(EntityStatus.ACTIVE, card.getEntityStatus());
    }

    @Nested
    class GetCardById {
        @Test
        void cardExist() {
            when(cardRepository.findByIdAndEntityStatus(card.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(card));

            assertSame(card, cardService.getCardById(card.getId()));
        }

        @Test
        void cardDoesNotExist() {
            var cardId = UUID.randomUUID();
            when(cardRepository.findByIdAndEntityStatus(cardId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> cardService.getCardById(cardId));
        }
    }
}
