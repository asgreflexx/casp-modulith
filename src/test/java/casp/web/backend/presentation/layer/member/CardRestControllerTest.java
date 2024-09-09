package casp.web.backend.presentation.layer.member;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.CardRepository;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.dtos.member.CardDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.member.CardMapper.CARD_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CardRestControllerTest {
    private static final String CARD_URL_PREFIX = "/card";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CardRepository cardRepository;
    private CardDto card;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        cardRepository.deleteAll();

        var cardDocument = TestFixture.createValidCard();
        memberRepository.save(cardDocument.getMember());
        card = CARD_MAPPER.toDto(cardRepository.save(cardDocument));
    }

    @Test
    void getCardsByMemberId() throws Exception {
        TypeReference<Set<CardDto>> typeReference = new TypeReference<>() {
        };

        var mvcResult = mockMvc.perform(get(CARD_URL_PREFIX + "/by-member-id/{memberId}", card.getMemberId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsExactly(card);
    }

    @Test
    void deleteCardById() throws Exception {
        mockMvc.perform(delete(CARD_URL_PREFIX + "/{id}", card.getId()))
                .andExpect(status().isOk());

        getCardById(card.getId()).andExpect(status().isBadRequest());
    }

    private ResultActions getCardById(final UUID id) throws Exception {
        return mockMvc.perform(get(CARD_URL_PREFIX + "/{id}", id));
    }

    @Nested
    class SaveCard {

        @Test
        void cardIsAlwaysAsActiveSaved() throws Exception {
            card.setEntityStatus(EntityStatus.DELETED);
            var mvcResult = postCard(card)
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, CardDto.class).getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
        }

        @Test
        void newCardIsInvalid() throws Exception {
            var unknownMember = CARD_MAPPER.toDto(TestFixture.createValidCard());

            postCard(unknownMember).andExpect(status().isBadRequest());
        }

        private ResultActions postCard(final CardDto card) throws Exception {
            return mockMvc.perform(post(CARD_URL_PREFIX)
                    .content(MvcMapper.toString(card))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetCardById {
        @Test
        void cardExists() throws Exception {
            var mvcResult = getCardById(CardRestControllerTest.this.card.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, CardDto.class)).isEqualTo(card);
        }

        @Test
        void cardDoesNotExists() throws Exception {
            getCardById(UUID.randomUUID()).andExpect(status().isBadRequest());
        }
    }
}
