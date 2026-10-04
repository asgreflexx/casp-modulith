package casp.web.backend.dog.presentation;

import casp.web.backend.MvcMapper;
import casp.web.backend.common.exception.DogHasHandlerConflictException;
import casp.web.backend.dog.DogHasHandlerService;
import casp.web.backend.dog.data.Grade;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(DogHasHandlerRestController.class)
class DogHasHandlerRestControllerExceptionIntTest {
    private static final String BASE_URL = "/dog-has-handler";
    private static final String DOG_HANDLER_BY_ID_URL = BASE_URL + "/{id}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DogHasHandlerService dogHasHandlerService;

    @Nested
    class GetDogHasHandlerById {

        @Test
        void badRequest(CapturedOutput output) throws Exception {
            final var expectedResponseMessage = "Method parameter 'id': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: invalid-id";
            var mvcResult = mockMvc.perform(get(DOG_HANDLER_BY_ID_URL, "invalid-id"))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            verifyNoInteractions(dogHasHandlerService);
            var problemDetail = MvcMapper.toObject(mvcResult, ProblemDetail.class);
            assertEquals(expectedResponseMessage, problemDetail.getDetail());
            assertThat(output).contains(Level.WARN.name(), "User did something wrong");
        }

        @Test
        void notFound(CapturedOutput output) throws Exception {
            var id = UUID.randomUUID();
            var expectedResponseMessage = "DogHasHandler with id %s not found or it isn't active".formatted(id);
            var exception = new NoSuchElementException(expectedResponseMessage);
            when(dogHasHandlerService.getDogHasHandlerById(id)).thenThrow(exception);

            var mvcResult = mockMvc.perform(get(DOG_HANDLER_BY_ID_URL, id))
                    .andExpect(status().isNotFound())
                    .andReturn();

            var problemDetail = MvcMapper.toObject(mvcResult, ProblemDetail.class);
            assertEquals(expectedResponseMessage, problemDetail.getDetail());
            assertThat(output).contains(Level.WARN.name(), "The user requested an element that does not exist");
        }
    }

    @Nested
    class SaveDogHasHandler {
        @Test
        void badRequest() throws Exception {
            var expectedResponseMessage = Arrays.stream("Field: dogId, rejected value: null, message: must not be null; Field: grades[].name, rejected value: null, message: must not be blank; Field: memberId, rejected value: null, message: must not be null; Field: grades[].type, rejected value: null, message: must not be null; Field: grades[].points, rejected value: 0, message: must be greater than 0; Field: grades[].examDate, rejected value: null, message: must not be null".split(";"))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            var dogHasHandlerWrite = new DogHasHandlerWrite();
            dogHasHandlerWrite.setGrades(Set.of(new Grade()));

            var mvcResult = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MvcMapper.toString(dogHasHandlerWrite)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            var problemDetail = MvcMapper.toObject(mvcResult, ProblemDetail.class);
            assertThat(problemDetail.getDetail())
                    .isNotBlank()
                    .satisfies(detail -> {
                        var actualResponseMessage = Arrays.stream(detail.split(";"))
                                .map(String::trim)
                                .collect(Collectors.toSet());
                        assertThat(actualResponseMessage).containsExactlyInAnyOrderElementsOf(expectedResponseMessage);
                    });
        }

        @Test
        void conflict(CapturedOutput output) throws Exception {
            var dogHasHandlerWrite = new DogHasHandlerWrite();
            dogHasHandlerWrite.setDogId(UUID.randomUUID());
            dogHasHandlerWrite.setMemberId(UUID.randomUUID());
            var dogHandlerException = new DogHasHandlerConflictException("boom");
            when(dogHasHandlerService.saveDogHasHandler(any())).thenThrow(dogHandlerException);

            var mvcResult = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MvcMapper.toString(dogHasHandlerWrite)))
                    .andExpect(status().isConflict())
                    .andReturn();

            var problemDetail = MvcMapper.toObject(mvcResult, ProblemDetail.class);
            assertThat(problemDetail.getDetail()).isEqualTo("boom");
            assertThat(output).contains(Level.WARN.name(), "A conflict encountered");
        }
    }

    @Test
    void deleteDogHasHandlerById() throws Exception {
        mockMvc.perform(delete(DOG_HANDLER_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDogHasHandlersByHandlerIds() throws Exception {
        mockMvc.perform(get(BASE_URL + "/by-ids").param("ids", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMembersEmailByIds() throws Exception {
        mockMvc.perform(get(BASE_URL + "/by-ids").param("ids", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDogHasHandlerByMemberId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/by-member-id/{memberId}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDogHasHandlerByDogId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/by-dog-id/{memberId}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
