package casp.web.backend.dog.presentation;

import casp.web.backend.MvcMapper;
import casp.web.backend.dog.DogService;
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

@WebMvcTest(DogRestController.class)
class DogRestControllerExceptionIntTest {
    private static final String BASE_URL = "/dog";
    private static final String DOG_BY_ID_URL = BASE_URL + "/{id}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DogService dogService;

    @Test
    void getDogById() throws Exception {
        mockMvc.perform(get(DOG_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveDog() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MvcMapper.toString(new DogWrite())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteDogById() throws Exception {
        mockMvc.perform(delete(DOG_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
