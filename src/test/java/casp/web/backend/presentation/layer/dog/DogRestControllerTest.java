package casp.web.backend.presentation.layer.dog;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.dog.DogDto;
import casp.web.backend.business.logic.layer.dog.DogService;
import casp.web.backend.common.dog.DogHasHandler;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.RestResponsePage;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.business.logic.layer.dog.DogMapper.DOG_MAPPER;
import static casp.web.backend.presentation.layer.dog.DogReadMapper.READ_MAPPER;
import static casp.web.backend.presentation.layer.dog.DogWriteMapper.WRITE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DogRestControllerTest {
    private static final String DOG_URL_PREFIX = "/dog";
    private static final String DOG_NOT_FOUND_MSG = "Dog with id %s not found or it isn't active.";
    private static final TypeReference<RestResponsePage<DogRead>> DOG_PAGE_RESPONSE = new TypeReference<>() {
    };

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;

    @SpyBean
    private DogService dogService;

    private DogDto charlie;
    private DogDto inactive;
    private Set<DogRead> expectedActiveDogs;

    @BeforeEach
    void setUp() {
        baseParticipantRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        dogRepository.deleteAll();
        memberRepository.deleteAll();

        charlie = createDog("Charlie", EntityStatus.ACTIVE);
        var bonsai = createDog("Bonsai", EntityStatus.ACTIVE);
        inactive = createDog("INACTIVE", EntityStatus.INACTIVE);
        expectedActiveDogs = READ_MAPPER.toTargetSet(Set.of(charlie, bonsai));
    }

    @Test
    void getDogs() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_URL_PREFIX)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andReturn();

        var dogReadPage = MvcMapper.toObject(mvcResult, DOG_PAGE_RESPONSE);
        assertThat(dogReadPage).containsExactlyInAnyOrderElementsOf(expectedActiveDogs);
    }

    @Test
    void register() throws Exception {
        var mvcResult = mockMvc.perform(post(DOG_URL_PREFIX + "/register"))
                .andExpect(status().isOk())
                .andReturn();

        var dogReadPage = MvcMapper.toObject(mvcResult, DOG_PAGE_RESPONSE);
        assertThat(dogReadPage).containsExactlyInAnyOrderElementsOf(expectedActiveDogs);
    }

    @Test
    void getDogByChipNumber() throws Exception {
        var mvcResult = mockMvc.perform(get(DOG_URL_PREFIX + "/by-chip-number/{chipNumber}", charlie.getChipNumber()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, Dog.class)).
                usingRecursiveAssertion()
                .isEqualTo(DOG_MAPPER.toSource(charlie));
    }

    private DogDto createDog(String name, EntityStatus entityStatus) {
        var dog = TestFixture.createDog();
        dog.setEntityStatus(entityStatus);
        dog.setName(name);
        dog.setChipNumber(UUID.randomUUID().toString());

        var member = TestFixture.createMember();
        member.setEntityStatus(entityStatus);
        memberRepository.save(member);

        var dogHasHandler = new casp.web.backend.data.access.layer.dog.DogHasHandler();
        memberReferenceRepository.findById(member.getId()).ifPresent(dogHasHandler::setMember);
        var dogReference = new DogReference();
        dogReference.setId(dog.getId());
        dogReference.setEntityStatus(entityStatus);
        dogReference.setName(dog.getName());
        dogHasHandler.setDog(dogReference);
        dogHasHandler.setEntityStatus(entityStatus);
        dogHasHandlerRepository.save(dogHasHandler);
        var space = TestFixture.createSpace();
        space.setMemberOrHandlerId(dogHasHandler.getId());
        space.setEntityStatus(entityStatus);
        var examParticipant = TestFixture.createExamParticipant();
        examParticipant.setMemberOrHandlerId(dogHasHandler.getId());
        examParticipant.setEntityStatus(entityStatus);
        baseParticipantRepository.saveAll(Set.of(examParticipant, space));

        var dto = DOG_MAPPER.toTarget(dogRepository.save(dog));
        var dogHasHandlerReferences = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());
        dto.setDogHasHandlerSet(DOG_MAPPER.toDogHasHandlerSet(dogHasHandlerReferences));
        return dto;
    }

    private ResultActions getDogById(UUID dogId) throws Exception {
        return mockMvc.perform(get(DOG_URL_PREFIX + "/{id}", dogId));
    }

    private void assertDogHasHandler(DogRead actual) {
        assertThat(actual.getDogHasHandlerSet())
                .singleElement()
                .satisfies(dh -> {
                    assertEquals(getDogHasHandler().getId(), dh.getId());
                    assertEquals(getDogHasHandler().getMemberId(), dh.getMemberId());
                    assertEquals(getDogHasHandler().getFirstName(), dh.getFirstName());
                    assertEquals(getDogHasHandler().getLastName(), dh.getLastName());
                });
    }

    private DogHasHandler getDogHasHandler() {
        return charlie.getDogHasHandlerSet().stream().findAny().orElseThrow();
    }

    @Nested
    class DeleteDogById {
        @Test
        void cascadeDelete() throws Exception {
            deleteDog(charlie.getId())
                    .andExpect(status().isNoContent());

//           FIXME getDogById(charlie.getId()).andExpect(status().isBadRequest());
//
//           var dogHasHandlerList = dogHasHandlerRepository.findAll()
//                    .stream()
//                    .filter(dh -> charlie.getId().equals(dh.getDog().getId()))
//                    .toList();
//            var baseParticipantList = baseParticipantRepository.findAll()
//                    .stream()
//                    .filter(p -> dogHasHandlerId.equals(p.getMemberOrHandlerId()))
//                    .toList();
//            assertThat(dogHasHandlerList).isNotEmpty().allSatisfy(dh -> assertSame(EntityStatus.DELETED, dh.getEntityStatus()));
//            assertThat(baseParticipantList).isNotEmpty().allSatisfy(p -> assertSame(EntityStatus.DELETED, p.getEntityStatus()));
        }

        @Test
        void dogNotFound() throws Exception {
            deleteDog(inactive.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DOG_NOT_FOUND_MSG.formatted(inactive.getId())));

            verify(dogService).deleteDogById(inactive.getId());
        }

        private ResultActions deleteDog(UUID dogId) throws Exception {
            return mockMvc.perform(delete(DOG_URL_PREFIX + "/{id}", dogId));
        }
    }

    @Nested
    class SaveDog {
        @Test
        void bodyIsInvalid() throws Exception {
            var exception = performPost(new DogWrite())
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("NotBlank.ownerName", "NotBlank.ownerAddress", "NotBlank.name"));
        }

        @Test
        void bodyIsValid() throws Exception {
            var mvcResult = performPost(WRITE_MAPPER.toTarget(charlie))
                    .andExpect(status().isOk())
                    .andReturn();

            var dogRead = MvcMapper.toObject(mvcResult, DogRead.class);
            assertThat(dogRead).isEqualTo(READ_MAPPER.toTarget(charlie));
        }

        private ResultActions performPost(DogWrite dog) throws Exception {
            return mockMvc.perform(post(DOG_URL_PREFIX)
                    .content(MvcMapper.toString(dog))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetDogsByNameOrOwnerName {
        private static final String URL_TEMPLATE = DOG_URL_PREFIX + "/by-dog-name-or-owner-name";

        @Test
        void foundDogByNameAndOwnerName() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE)
                            .param("name", charlie.getName())
                            .param("ownerName", charlie.getOwnerName()))
                    .andExpect(status().isOk())
                    .andReturn();

            var dogPage = MvcMapper.toObject(mvcResult, DOG_PAGE_RESPONSE);
            assertThat(dogPage)
                    .singleElement()
                    .usingRecursiveAssertion()
                    .isEqualTo(READ_MAPPER.toTarget(charlie));
        }

        @Test
        void withoutParameters() throws Exception {
            var mvcResult = mockMvc.perform(get(URL_TEMPLATE))
                    .andExpect(status().isOk())
                    .andReturn();

            var dogPage = MvcMapper.toObject(mvcResult, DOG_PAGE_RESPONSE);
            assertThat(dogPage)
                    .allSatisfy(actual -> assertThat(expectedActiveDogs)
                            .anySatisfy(expected -> assertThat(actual)
                                    .usingRecursiveAssertion()
                                    .isEqualTo(expected)));
        }
    }

    @Nested
    class GetDogById {
        @Test
        void dogFound() throws Exception {
            var mvcResult = getDogById(charlie.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            var dogRead = MvcMapper.toObject(mvcResult, DogRead.class);
            assertThat(dogRead).isEqualTo(READ_MAPPER.toTarget(charlie));
            assertNotNull(dogRead.getSpaces());
            assertDogHasHandler(dogRead);
        }

        @Test
        void dogNotFound() throws Exception {
            getDogById(inactive.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DOG_NOT_FOUND_MSG.formatted(inactive.getId())));
        }
    }
}
