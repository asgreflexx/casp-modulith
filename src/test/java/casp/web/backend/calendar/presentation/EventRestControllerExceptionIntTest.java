package casp.web.backend.calendar.presentation;

import casp.web.backend.MvcMapper;
import casp.web.backend.calendar.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventRestController.class)
class EventRestControllerExceptionIntTest {
    private static final String BASE_URL = "/event";
    private static final String EVENT_BY_ID_URL = BASE_URL + "/{id}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EventService eventService;

    @Test
    void save() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MvcMapper.toString(new EventWrite())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete(EVENT_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOneById() throws Exception {
        mockMvc.perform(get(EVENT_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
