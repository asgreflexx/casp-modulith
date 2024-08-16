package casp.web.backend.presentation.layer;

import casp.web.backend.data.access.layer.entities.Card;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.repositories.ICardRepository;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static casp.web.backend.presentation.layer.ObjectStringMapper.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CardRestControllerTest {

    @Autowired
    private IMemberRepository memberRepository;

    @Autowired
    private ICardRepository cardRepository;

    @Autowired
    private MockMvc mockMvc;

    private Member member;
    private Card card;

    @BeforeEach
    void before() {
        cardRepository.deleteAll();
        memberRepository.deleteAll();

        member = new Member();
        member.setFirstName("Jane");
        member.setLastName("Austen");
        member.setEmail("jane@gmail.com");
        member = memberRepository.save(member);

        card = new Card();
        card.setCode("bcd");
        card.setMemberId(member.getId());
        card.setBalance(123d);
        card = cardRepository.save(card);
    }

    @Test
    void saveCard() throws Exception {

        Member member1 = new Member();
        member1.setFirstName("Jane");
        member1.setLastName("Austen");
        member1.setEmail("jane@gmail.com");
        member1 = memberRepository.save(member1);

        Card card = new Card();
        card.setCode("bcd");
        card.setMemberId(member1.getId());
        card.setBalance(123d);

        mockMvc.perform(post("/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(card)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId().toString()))
                .andExpect(jsonPath("$.code").value("bcd"));
    }

    @Test
    void getCardsByMemberId() throws Exception {
        mockMvc.perform(get("/card/member_id/{id}", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(card.getId().toString()))
                .andExpect(jsonPath("$[0].code").value(card.getCode()))
                .andExpect(jsonPath("$[0].memberId").value(member.getId().toString()));
    }

    @Test
    void deleteCardById() throws Exception {
        mockMvc.perform(delete("/card/{id}", card.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Card was successfully deleted!"));
    }

    @Test
    void getCardById() throws Exception {
        mockMvc.perform(get("/card/{id}", card.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId().toString()))
                .andExpect(jsonPath("$.code").value(card.getCode()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.memberId").value(member.getId().toString()));
    }
}
