package casp.web.backend.presentation.layer.dog;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import casp.web.backend.data.access.layer.repositories.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.DogRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.RestResponsePage;
import casp.web.backend.presentation.layer.dtos.dog.DogDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.dog.DogMapper.DOG_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DogRestControllerTest {
    private static final String DOG_URL_PREFIX = "/dog";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;

    private DogDto charlie;
    private DogDto bonsai;
    private DogDto inactive;

    @BeforeEach
    void setUp() {
        baseParticipantRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        dogRepository.deleteAll();

        charlie = createDog("Charlie", EntityStatus.ACTIVE);
        bonsai = createDog("Bonsai", EntityStatus.ACTIVE);
        inactive = createDog("INACTIVE", EntityStatus.INACTIVE);
    }

    @Test
    void getDogs() throws Exception {
        TypeReference<RestResponsePage<DogDto>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(DOG_URL_PREFIX)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andReturn();

        var dogDtoPage = MvcMapper.toObject(mvcResult, typeReference);
        assertThat(dogDtoPage.getContent()).containsExactly(charlie, bonsai);
    }

    private DogDto createDog(final String name, final EntityStatus entityStatus) {
        var dog = TestFixture.createValidDog();
        dog.setEntityStatus(entityStatus);
        dog.setName(name);
        dog.setChipNumber(UUID.randomUUID().toString());
        return DOG_MAPPER.toDto(dogRepository.save(dog));
    }

    private ResultActions getDogById(final UUID dogId) throws Exception {
        return mockMvc.perform(get(DOG_URL_PREFIX + "/{id}", dogId));
    }

    @Nested
    class DeleteDataById {
        @Test
        void cascadeDelete() throws Exception {
            var dogHasHandler = TestFixture.createValidDogHasHandler();
            dogHasHandler.setDog(charlie);
            dogHasHandler.setDogId(charlie.getId());
            dogHasHandlerRepository.save(dogHasHandler);
            var space = TestFixture.createValidSpace();
            space.setMemberOrHandlerId(dogHasHandler.getId());
            var examParticipant = TestFixture.createValidExamParticipant();
            examParticipant.setMemberOrHandlerId(dogHasHandler.getId());
            baseParticipantRepository.saveAll(Set.of(examParticipant, space));

            deleteDog(charlie.getId())
                    .andExpect(status().isOk());

            getDogById(charlie.getId()).andExpect(status().isBadRequest());
            assertThat(dogHasHandlerRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.DELETED, dh.getEntityStatus()));
            assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.DELETED, p.getEntityStatus()));
        }

        @Test
        void dogNotFound() throws Exception {
            deleteDog(inactive.getId())
                    .andExpect(status().isBadRequest());
        }

        private ResultActions deleteDog(final UUID dogId) throws Exception {
            return mockMvc.perform(delete(DOG_URL_PREFIX + "/{id}", dogId));
        }
    }

    @Nested
    class PostData {
        @Test
        void dogIsAlwaysAsActiveSaved() throws Exception {
            charlie.setEntityStatus(EntityStatus.DELETED);

            performPost(charlie)
                    .andExpect(status().isOk());

            var mvcResult = getDogById(charlie.getId())
                    .andExpect(status().isOk())
                    .andReturn();
            assertThat(MvcMapper.toObject(mvcResult, DogDto.class))
                    .satisfies(dogDto -> assertSame(EntityStatus.ACTIVE, dogDto.getEntityStatus()));
        }

        @Test
        void bodyIsInvalid() throws Exception {
            charlie.setName(null);

            performPost(charlie)
                    .andExpect(status().isBadRequest());
        }

        private ResultActions performPost(final DogDto dogDto) throws Exception {
            return mockMvc.perform(post(DOG_URL_PREFIX)
                    .content(MvcMapper.toString(dogDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetDogsByOwner {

        private static final TypeReference<List<DogDto>> TYPE_REFERENCE = new TypeReference<>() {
        };
        private static final String URL_TEMPLATE = DOG_URL_PREFIX + "/by-chip-number-or-dog-name-or-owner-name";

        @Test
        void foundDogByChipNumber() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE)
                            .param("chipNumber", charlie.getChipNumber()))
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, TYPE_REFERENCE)).containsExactly(charlie);
        }

        @Test
        void foundDogByNameAndOwnerName() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE)
                            .param("name", charlie.getName())
                            .param("ownerName", charlie.getOwnerName()))
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, TYPE_REFERENCE)).containsExactly(charlie);
        }

        @Test
        void dogNotFoundByChipNumber() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE)
                            .param("chipNumber", inactive.getChipNumber()))
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, TYPE_REFERENCE)).isEmpty();
        }

        @Test
        void withoutParameters() throws Exception {
            mockMvc.perform(get(URL_TEMPLATE))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withOnlyDogName() throws Exception {
            mockMvc.perform(get(URL_TEMPLATE)
                            .param("name", charlie.getName()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withOnlyOwnerName() throws Exception {
            mockMvc.perform(get(URL_TEMPLATE)
                            .param("ownerName", charlie.getOwnerName()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetDogById {
        @Test
        void dogFound() throws Exception {
            var mvcResult = getDogById(charlie.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, DogDto.class)).isEqualTo(charlie);
        }

        @Test
        void dogNotFound() throws Exception {
            getDogById(DogRestControllerTest.this.inactive.getId())
                    .andExpect(status().isBadRequest());
        }
    }
}
