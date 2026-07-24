package casp.web.backend.calendar.presentation;

import casp.web.backend.MvcMapper;
import casp.web.backend.calendar.CourseService;
import casp.web.backend.calendar.data.participants.EventResponse;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseRestController.class)
class CourseRestControllerExceptionIntTest {
    private static final String BASE_URL = "/course";
    private static final String COURSE_BY_ID_URL = BASE_URL + "/{id}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CourseService courseService;

    @Test
    void save() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MvcMapper.toString(new CourseWrite())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOneById() throws Exception {
        mockMvc.perform(get(COURSE_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete(COURSE_BY_ID_URL, "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByYear() throws Exception {
        mockMvc.perform(get(BASE_URL).param("year", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSpacesEmail() throws Exception {
        mockMvc.perform(get(BASE_URL + "/emails/{id}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Nested
    class UpdateSpaces {
        private static final String URI_TEMPLATE = BASE_URL + "/{courseId}/spaces";

        @Nested
        class BadRequest {
            @Test
            void badCourseId() throws Exception {
                mockMvc.perform(patch(URI_TEMPLATE, "invalid-id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MvcMapper.toString(Set.of(new SpaceWrite()))))
                        .andExpect(status().isBadRequest());
            }

            @Test
            void missingVersionHeader() throws Exception {
                mockMvc.perform(patch(URI_TEMPLATE, UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MvcMapper.toString(Set.of(new SpaceWrite()))))
                        .andExpect(status().isBadRequest());
            }

            @Test
            void badBody() throws Exception {
                mockMvc.perform(patch(URI_TEMPLATE, UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("If-Match", "1")
                                .content(MvcMapper.toString(Set.of(new SpaceWrite()))))
                        .andExpect(status().isBadRequest());
            }
        }

        @Test
        void conflict() throws Exception {
            var courseId = UUID.randomUUID();
            var spaceWriteSet = createSpaceWriteSet();
            when(courseService.updateSpaces(eq(courseId), eq(1L), anySet()))
                    .thenThrow(new OptimisticLockingFailureException("boom"));

            mockMvc.perform(patch(URI_TEMPLATE, courseId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("If-Match", "1")
                            .content(MvcMapper.toString(spaceWriteSet)))
                    .andExpect(status().isConflict());
        }

        private static Set<SpaceWrite> createSpaceWriteSet() {
            var member = new MemberReference();
            member.setFirstName("John");
            member.setLastName("Doe");
            member.setEmail("email@email.com");
            var dog = new DogReference();
            dog.setName("Riley");
            dog.setOwnerName("John Doe");
            var dogHasHandler = new DogHasHandlerReference();
            dogHasHandler.setDog(dog);
            dogHasHandler.setMember(member);
            var spaceWrite = new SpaceWrite();
            spaceWrite.setDogHasHandler(dogHasHandler);
            spaceWrite.setResponse(EventResponse.ACCEPTED);
            return Set.of(spaceWrite);
        }
    }

    @Test
    void getCoursesByDogHasHandlerId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/space/{dogHasHandlerId}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
