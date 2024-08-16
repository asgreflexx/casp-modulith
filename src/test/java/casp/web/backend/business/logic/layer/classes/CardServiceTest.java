package casp.web.backend.business.logic.layer.classes;

import casp.web.backend.data.access.layer.entities.Card;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.ICardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private ICardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Card card;
    private UUID id;
    private UUID randomId;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        randomId = UUID.randomUUID();
        //Rep.delete all nicht notwendig, da gemockt

        // Nicht notwendig, einen Member zu erstellen, er wird gemockt

        card = new Card();
        card.setBalance(24.50);
        card.setCode("123456");
        card.setId(id);
    }

    @Test
    void deleteCardByIdHappyPath() throws Exception {
        //given
        Mockito.when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        card.setEntityStatus(EntityStatus.DELETED);
        Mockito.when(cardRepository.save(card)).thenReturn(card);

        // when
        cardService.deleteCardById(id);

        // then, verify überprüft ob die Funktion benutzt wird - mind. 1 mal verwendet(defaultwert)
        // inOrder - wurde auch in dieser Reihenfolge durchgeführt
        Mockito.verify(cardRepository).save(card);
    }

    @Test
    void deleteCardByIdCardNotFound() {
        // given
        Mockito.when(cardRepository.findById(randomId)).thenReturn(Optional.empty());

        // when + then(Exception)
        assertThrows(Exception.class, () -> cardService.deleteCardById(randomId));
    }

    // Happy path testing is a well-defined test case using known input,
    // which executes without exception and produces an expected output.
    @Test
    void getCardByIdHappyPath() throws Exception {
        // given, hier ist die Funktion setUp ist ein Teil davon, da die Card im setUp definiert wurde
        Mockito.when(cardRepository.findById(id)).thenReturn(Optional.of(card));

        // when
        Card actualCard = cardService.getCardById(id);

        // then
        assertEquals(card.getBalance(), actualCard.getBalance());
        assertEquals(card.getCode(), actualCard.getCode());
    }

    @Test
    void getCardByIdCardNotFound() {
        // given
        Mockito.when(cardRepository.findById(randomId)).thenReturn(Optional.empty());

        // when + then(Exception)
        assertThrows(Exception.class, () -> cardService.getCardById(randomId));
    }
}
