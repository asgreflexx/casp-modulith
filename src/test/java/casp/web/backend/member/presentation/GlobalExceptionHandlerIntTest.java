package casp.web.backend.member.presentation;

import casp.web.backend.MvcMapper;
import casp.web.backend.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(MemberRestController.class)
class GlobalExceptionHandlerIntTest {
    private static final String BASE_URL = "/member";
    private static final String TOGGLE_STATUS_URL = BASE_URL + "/toggle-status/{id}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MemberService memberService;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
    }

    @Test
    void methodNotSupportedException(CapturedOutput output) throws Exception {
        var mvcResult = mockMvc.perform(delete(TOGGLE_STATUS_URL, memberId.toString()))
                .andExpect(status().isMethodNotAllowed())
                .andReturn();

        var problemDetail = MvcMapper.toObject(mvcResult, ProblemDetail.class);
        assertThat(problemDetail.getDetail()).isEqualTo("Request method 'DELETE' is not supported");
        assertThat(output).contains(Level.WARN.name(), "The user made an unsupported call");
    }

    @Test
    void handleException(CapturedOutput output) throws Exception {
        when(memberService.toggleStatus(memberId)).thenThrow(new RuntimeException("boom"));

        var mvcResult = mockMvc.perform(post(TOGGLE_STATUS_URL, memberId.toString()))
                .andExpect(status().isInternalServerError())
                .andReturn();

        var problemDetail = MvcMapper.toObject(mvcResult, ProblemDetail.class);
        assertThat(problemDetail.getDetail()).isEqualTo("An internal error has occurred");
        assertThat(output).contains(Level.ERROR.name(), "Something went wrong");
    }
}
