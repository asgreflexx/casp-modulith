package casp.web.backend.calendar.presentation;

import casp.web.backend.MvcMapper;
import casp.web.backend.calendar.ExamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExamRestController.class)
class ExamRestControllerExceptionIntTest {
    private static final String BASE_URL = "/exam";
    private static final String EXAM_BY_ID_URL = BASE_URL + "/{id}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ExamService examService;

    @Test
    void save() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MvcMapper.toString(new ExamWrite())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete(EXAM_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getExamsByDogHasHandlerId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/participants/{dogHasHandlerId}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOneById() throws Exception {
        mockMvc.perform(get(EXAM_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
