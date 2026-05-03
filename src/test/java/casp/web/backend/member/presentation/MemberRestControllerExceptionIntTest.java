package casp.web.backend.member.presentation;

import casp.web.backend.MvcMapper;
import casp.web.backend.common.exception.MemberEMailConflictException;
import casp.web.backend.common.exception.MemberStateConflictException;
import casp.web.backend.member.MemberService;
import casp.web.backend.member.data.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(MemberRestController.class)
class MemberRestControllerExceptionIntTest {
    private static final String BASE_URL = "/member";
    private static final String MEMBER_BY_ID_URL = BASE_URL + "/{id}";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MemberService memberService;

    @Test
    void getMembers() throws Exception {
        mockMvc.perform(get(BASE_URL).param("entityStatusParam", "invalid-status"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMemberById() throws Exception {
        mockMvc.perform(get(MEMBER_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Nested
    class SaveMember {
        private MemberWrite memberWrite;

        @BeforeEach
        void setUp() {
            memberWrite = new MemberWrite();
            memberWrite.setFirstName("Max");
            memberWrite.setLastName("Mustermann");
            memberWrite.setEmail("mail@mail.com");
            memberWrite.setRoles(Set.of(Role.USER));
        }

        @Test
        void badRequest() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MvcMapper.toString(new MemberWrite())))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void memberEMailConflictException(CapturedOutput output) throws Exception {
            when(memberService.saveMember(any())).thenThrow(new MemberEMailConflictException("boom"));

            var mvcResult = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MvcMapper.toString(memberWrite)))
                    .andExpect(status().isConflict())
                    .andReturn();

            var problemDetail = MvcMapper.toObject(mvcResult, ProblemDetail.class);
            assertThat(problemDetail.getDetail()).isEqualTo("boom");
            assertThat(output).contains(Level.WARN.name(), "A conflict encountered");
        }

        @Test
        void memberStateConflictException(CapturedOutput output) throws Exception {
            when(memberService.saveMember(any())).thenThrow(new MemberStateConflictException("boom"));

            var mvcResult = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MvcMapper.toString(memberWrite)))
                    .andExpect(status().isConflict())
                    .andReturn();

            var problemDetail = MvcMapper.toObject(mvcResult, ProblemDetail.class);
            assertThat(problemDetail.getDetail()).isEqualTo("boom");
            assertThat(output).contains(Level.WARN.name(), "A conflict encountered");
        }
    }

    @Test
    void deleteMember() throws Exception {
        mockMvc.perform(delete(MEMBER_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMembersEmailByIds() throws Exception {
        mockMvc.perform(get(BASE_URL + "/emails-by-ids").param("membersId", "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void toggleStatus() throws Exception {
        mockMvc.perform(post(BASE_URL + "/toggle-status/{id}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
