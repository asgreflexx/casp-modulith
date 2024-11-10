package casp.web.backend.dog.presentation;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.dog.DogHasHandlerOldRepository;
import casp.web.backend.dog.DogHasHandlerDto;
import casp.web.backend.dog.DogHasHandlerService;
import casp.web.backend.dog.data.DogHasHandler;
import casp.web.backend.dog.data.DogHasHandlerRepository;
import casp.web.backend.dog.data.Grade;
import casp.web.backend.dog.data.GradeType;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.RestResponsePage;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.dog.presentation.DogHasHandlerReadMapper.READ_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DogHasHandlerRestControllerTest {
    private static final String DOG_HAS_HANDLER_URL_PREFIX = "/dog-has-handler";
    private static final String DOG_NAME = "Riley";
    private static final TypeReference<RestResponsePage<DogHasHandlerRead>> DOG_HAS_HANDLER_PAGE = new TypeReference<>() {
    };
    private static final String IDS_SET_IS_NOT_PRESENT = "Required request parameter 'ids' for method parameter type Set is not present";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DogHasHandlerOldRepository dogHasHandlerOldRepository;
    @Autowired
    private DogReferenceRepository dogReferenceRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;

    @SpyBean
    private DogHasHandlerService dogHasHandlerService;

    private DogHasHandlerDto dogHasHandlerDto;

    private static String createMessageDogHasHandlerDoesNotExist(UUID id) {
        return "DogHasHandler with id %s not found or it isn't active".formatted(id);
    }

    @BeforeEach
    void setUp() {
        dogHasHandlerOldRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        memberReferenceRepository.deleteAll();
        dogReferenceRepository.deleteAll();


        var member = memberReferenceRepository.save(ReferenceTestFixture.createMemberReference());
        var dog = dogReferenceRepository.save(ReferenceTestFixture.createDogReference());
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setDog(dog);
        dogHasHandler.setMember(member);
        dogHasHandler = dogHasHandlerRepository.save(dogHasHandler);
        dogHasHandlerDto = DOG_HAS_HANDLER_MAPPER.toTarget(dogHasHandler);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {DOG_NAME})
    void searchByName(String name) throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/search-by-name")
                        .param("name", name))
                .andExpect(status().isOk())
                .andReturn();

        var dogHasHandlerPage = MvcMapper.toObject(mvcResult, DOG_HAS_HANDLER_PAGE);
        assertThat(dogHasHandlerPage)
                .contains(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void getAllDogHasHandlers() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX))
                .andExpect(status().isOk())
                .andReturn();

        var dogHasHandlerPage = MvcMapper.toObject(mvcResult, DOG_HAS_HANDLER_PAGE);
        assertThat(dogHasHandlerPage)
                .contains(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void migrateDataToV2() throws Exception {
        dogHasHandlerRepository.deleteAll();
        var dogHasHandler = new casp.web.backend.deprecated.dog.DogHasHandler();
        dogHasHandler.setDogId(dogHasHandlerDto.getDog().getId());
        dogHasHandler.setMemberId(dogHasHandlerDto.getMember().getId());
        dogHasHandlerOldRepository.save(dogHasHandler);

        mockMvc.perform(post(DOG_HAS_HANDLER_URL_PREFIX + "/migrate-data"))
                .andExpect(status().isNoContent());

        verify(dogHasHandlerService).migrateDataToV2();
    }

    private void assertDogAndMemberFields(DogHasHandlerRead dhh) {
        assertThat(dhh.getDog())
                .usingRecursiveAssertion()
                .isEqualTo(dogHasHandlerDto.getDog());
        assertThat(dhh.getMember())
                .usingRecursiveAssertion()
                .isEqualTo(dogHasHandlerDto.getMember());
    }

    @Nested
    class GetMembersEmailByIds {
        private static final String URL_TEMPLATE = DOG_HAS_HANDLER_URL_PREFIX + "/emails-by-ids";

        @Test
        void callWithIds() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE)
                            .param("ids", dogHasHandlerDto.getId().toString()))
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, new TypeReference<Set<String>>() {
            })).containsOnly(dogHasHandlerDto.getMember().getEmail());
        }

        @Test
        void callWithoutIds() throws Exception {
            mockMvc.perform(get(URL_TEMPLATE))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(IDS_SET_IS_NOT_PRESENT));

            verifyNoInteractions(dogHasHandlerService);
        }
    }

    @Nested
    class GetDogHasHandlersByHandlerIds {
        private static final String URL_TEMPLATE = DOG_HAS_HANDLER_URL_PREFIX + "/by-ids";

        @Test
        void callWithIds() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE)
                            .param("ids", dogHasHandlerDto.getId().toString()))
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, new TypeReference<Set<DogHasHandlerRead>>() {
            })).containsOnly(READ_MAPPER.toTarget(dogHasHandlerDto));
        }

        @Test
        void callWithoutIds() throws Exception {
            mockMvc.perform(get(URL_TEMPLATE))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(IDS_SET_IS_NOT_PRESENT));

            verifyNoInteractions(dogHasHandlerService);
        }
    }

    @Nested
    class DeleteDogHasHandlerById {
        @Test
        void itExist() throws Exception {
            deleteDogHasHandlerById(dogHasHandlerDto.getId())
                    .andExpect(status().isNoContent());

            var dogHasHandler = dogHasHandlerRepository.findById(dogHasHandlerDto.getId());
            assertThat(dogHasHandler)
                    .map(BaseDocument::getEntityStatus)
                    .hasValue(EntityStatus.DELETED);
        }

        @Test
        void doesNotExist() throws Exception {
            var id = UUID.randomUUID();
            deleteDogHasHandlerById(id)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(createMessageDogHasHandlerDoesNotExist(id)));

            verify(dogHasHandlerService).deleteDogHasHandlerById(id);
        }

        private ResultActions deleteDogHasHandlerById(UUID id) throws Exception {
            return mockMvc.perform(delete(DOG_HAS_HANDLER_URL_PREFIX + "/{id}", id));
        }
    }

    @Nested
    class SaveDogHasHandler {
        @Test
        void dogHasHandlerIsInvalid() throws Exception {
            var exception = performPost(new DogHasHandlerWrite())
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("NotNull.dogId", "NotNull.memberId"));
        }

        @Test
        void cardIsInvalid() throws Exception {
            var dogHasHandlerWrite = createDogHasHandlerWrite();
            dogHasHandlerWrite.setGrades(Set.of(new Grade()));

            var exception = performPost(dogHasHandlerWrite)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("NotBlank.name", "NotNull.type", "Positive.points", "NotNull.examDate"));

        }

        @Test
        void cardIsValid() throws Exception {
            var dogHasHandlerWrite = createDogHasHandlerWrite();
            var grade = new Grade();
            grade.setName("name");
            grade.setType(GradeType.BH1);
            grade.setPoints(1);
            grade.setExamDate(LocalDate.now());
            dogHasHandlerWrite.setGrades(Set.of(grade));

            var mvcResult = performPost(dogHasHandlerWrite)
                    .andExpect(status().isOk())
                    .andReturn();

            var dogHasHandlerRead = MvcMapper.toObject(mvcResult, DogHasHandlerRead.class);
            assertThat(dogHasHandlerRead)
                    .satisfies(dhh -> {
                        assertDogAndMemberFields(dhh);
                        assertThat(dhh.getGrades())
                                .singleElement()
                                .usingRecursiveAssertion()
                                .isEqualTo(grade);
                    });
        }

        @Test
        void cardIsNull() throws Exception {
            var dogHasHandlerWrite = createDogHasHandlerWrite();
            dogHasHandlerWrite.setGrades(null);

            var mvcResult = performPost(dogHasHandlerWrite)
                    .andExpect(status().isOk())
                    .andReturn();

            var dogHasHandlerRead = MvcMapper.toObject(mvcResult, DogHasHandlerRead.class);
            assertThat(dogHasHandlerRead)
                    .satisfies(dhh -> {
                        assertDogAndMemberFields(dhh);
                        assertThat(dhh.getGrades())
                                .isEmpty();
                    });
        }

        private DogHasHandlerWrite createDogHasHandlerWrite() {
            var dogHasHandlerWrite = new DogHasHandlerWrite();
            dogHasHandlerWrite.setId(dogHasHandlerDto.getId());
            dogHasHandlerWrite.setVersion(dogHasHandlerDto.getVersion());
            dogHasHandlerWrite.setDogId(dogHasHandlerDto.getDog().getId());
            dogHasHandlerWrite.setMemberId(dogHasHandlerDto.getMember().getId());
            return dogHasHandlerWrite;
        }

        private ResultActions performPost(DogHasHandlerWrite dogHasHandlerWrite) throws Exception {
            return mockMvc.perform(post(DOG_HAS_HANDLER_URL_PREFIX)
                    .content(MvcMapper.toString(dogHasHandlerWrite))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetDogHasHandlerById {
        @Test
        void itExist() throws Exception {
            var mvcResult = getDogHasHandlerById(dogHasHandlerDto.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            var dogHasHandlerRead = MvcMapper.toObject(mvcResult, DogHasHandlerRead.class);
            assertThat(dogHasHandlerRead)
                    .satisfies(dhh -> {
                        assertDogAndMemberFields(dhh);
                        assertThat(dhh.getGrades()).isEmpty();
                    });
        }

        @Test
        void doesNotExist() throws Exception {
            var id = UUID.randomUUID();
            getDogHasHandlerById(id)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(createMessageDogHasHandlerDoesNotExist(id)));

            verify(dogHasHandlerService).getDogHasHandlerById(id);
        }

        private ResultActions getDogHasHandlerById(UUID id) throws Exception {
            return mockMvc.perform(get(DOG_HAS_HANDLER_URL_PREFIX + "/{id}", id));
        }
    }
}
