package casp.web.backend.presentation.layer.member;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.member.CardRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CardRestControllerTest {
    private static final String CARD_URL_PREFIX = "/card";
    private static final String CARD_NOT_FOUND_MESSAGE = "Card with id %s not found or it isn't %s.";

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

        var cardDocument = TestFixture.createCard();
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

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).
                singleElement()
                .satisfies(cardDto -> assertEquals(card.getId(), cardDto.getId()));
    }

    private ResultActions getCardById(final UUID id) throws Exception {
        return mockMvc.perform(get(CARD_URL_PREFIX + "/{id}", id));
    }

    @Nested
    class DeleteCardById {
        @Test
        void exist() throws Exception {
            performDelete(card.getId())
                    .andExpect(status().isNoContent());

            assertThat(cardRepository.findAll())
                    .singleElement()
                    .satisfies(c -> assertEquals(EntityStatus.DELETED, c.getEntityStatus()));
        }

        @Test
        void doesNotExist() throws Exception {
            var id = UUID.randomUUID();
            performDelete(id)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(CARD_NOT_FOUND_MESSAGE.formatted(id, EntityStatus.ACTIVE)));
        }

        private ResultActions performDelete(final UUID id) throws Exception {
            return mockMvc.perform(delete(CARD_URL_PREFIX + "/{id}", id));
        }
    }

    @Nested
    class SaveCard {

        @Test
        void cardIsAlwaysAsActiveSaved() throws Exception {
            var mvcResult = postCard(card)
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(card.getId(), MvcMapper.toObject(mvcResult, CardDto.class).getId());
        }

        @Test
        void cardMemberIsUnknown() throws Exception {
            var cardWithUnknownMember = CARD_MAPPER.toDto(TestFixture.createCard());

            postCard(cardWithUnknownMember)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value("Member with id %s not found or it isn't %s.".formatted(cardWithUnknownMember.getMemberId(), EntityStatus.ACTIVE)));
        }

        @Test
        void cardIsInvalid() throws Exception {
            var exception = postCard(new CardDto())
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("NotNull.memberId", "NotBlank.code"));
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
            var mvcResult = getCardById(card.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(card.getId(), MvcMapper.toObject(mvcResult, CardDto.class).getId());
        }

        @Test
        void cardDoesNotExists() throws Exception {
            var id = UUID.randomUUID();
            getCardById(id)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(CARD_NOT_FOUND_MESSAGE.formatted(id, EntityStatus.ACTIVE)));
        }
    }
}
