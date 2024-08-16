package casp.web.backend.presentation.layer;

import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.entities.MembershipFee;
import casp.web.backend.data.access.layer.enumerations.Roles;
import casp.web.backend.data.access.layer.repositories.ICardRepository;
import casp.web.backend.data.access.layer.repositories.IDogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.IDogRepository;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberRestControllerTest {

    @Autowired
    private IMemberRepository memberRepository;

    @Autowired
    private IDogRepository dogRepository;

    @Autowired
    private IDogHasHandlerRepository dogHasHandlerRepository;

    @Autowired
    private ICardRepository cardRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private MembershipFee membershipFee;
    private Roles role;

    @BeforeEach
    void before() {

        dogHasHandlerRepository.deleteAll();
        cardRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();

        role = Roles.ADMIN;

        membershipFee = new MembershipFee();
        membershipFee.setPaid(false);
        membershipFee.setPaidPrice(123f);

        member = new Member();
        member.setFirstName("Ciao");
        member.setLastName("Bella");
        member.setEmail("ciao@gmail.com");
        member.getRoles().add(role);
        member.getMembershipFees().add(membershipFee);
        member = memberRepository.save(member);
    }

    @Test
    void getData() throws Exception {

        mockMvc.perform(get("/member/page")
                        .param("page", "0")
                        .param("numberOfElements", "all")
                        .param("entityStatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(member.getId().toString()))
                .andExpect(jsonPath("$.content[0].firstName").value("Ciao"));

    }

    @Test
    void getDataById() throws Exception {

        mockMvc.perform(get("/member/{id}", member.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(member.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("Ciao"));
    }

    @Test
    void getDataByFirstNameAndLastName() throws Exception {

        mockMvc.perform(get("/member/searchFnLn")
                        .param("firstName", "Ciao")
                        .param("lastName", "Bella"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(member.getId().toString()))
                .andExpect(jsonPath("$[0].firstName").value("Ciao"));
    }

    @Test
    void saveMember() throws Exception {

        Member member1 = new Member();
        member1.setFirstName("Sir");
        member1.setLastName("Arthur");
        member1.setEmail("sir@gmail.com");
        member1.getRoles().add(role);

        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectStringMapper.asJsonString(member1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(member1.getFirstName()));
    }

    @Test
    void saveMemberTwice() throws Exception {

        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ObjectStringMapper.asJsonString(member)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(member.getFirstName()))
                .andExpect(jsonPath("$.version").value(member.getVersion() + 1));
    }

    @Test
    void deleteMethod() throws Exception {
        mockMvc.perform(delete("/member/{id}", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Member was deleted"));

    }

    @Test
    void inactiveMethod() throws Exception {

        mockMvc.perform(put("/member/{id}/deactivate", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Member was deactivated"));
    }

    @Test
    void activateMember() throws Exception {

        mockMvc.perform(put("/member/{id}/activate", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Member was activated"));
    }

    @Test
    void searchMembersByFirstNameOrLastName() throws Exception {

        mockMvc.perform(get("/member/searchMemberByName")
                        .param("firstName", "")
                        .param("lastName", "Bella"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].lastName").value(member.getLastName()));
    }

    @Test
    void getRoles() throws Exception {

        mockMvc.perform(get("/member/getRoles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("USER"));
    }

    @Test
    void returnMemberEmailHappyPath() throws Exception {
        mockMvc.perform(get("/member/get-emails-by-ids")
                        .param("membersId", member.getId().toString(), UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(member.getEmail()));
    }

    @Test
    void doesNotReturnMemberEmailByUnknownId() throws Exception {
        mockMvc.perform(get("/member/get-emails-by-ids")
                        .param("membersId", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void doesNotReturnMemberEmailParameterIsNull() throws Exception {
        mockMvc.perform(get("/member/get-emails-by-ids"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "XXX"})
    void doesNotReturnMemberEmailParameterIsEmptyOrBad(String id) throws Exception {
        mockMvc.perform(get("/member/get-emails-by-ids")
                        .param("membersId", id))
                .andExpect(status().isBadRequest());
    }
}
