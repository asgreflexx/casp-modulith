package casp.web.backend.presentation.layer;

import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.entities.DogHasHandler;
import casp.web.backend.data.access.layer.entities.Grade;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.GradeType;
import casp.web.backend.data.access.layer.enumerations.Roles;
import casp.web.backend.data.access.layer.repositories.ICardRepository;
import casp.web.backend.data.access.layer.repositories.IDogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.IDogRepository;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static casp.web.backend.presentation.layer.ObjectStringMapper.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DogHasHandlerRestControllerTest {

    private static final String DOGHASHANDLER_BASE_URL = "/doghashandler";
    @Autowired
    private IMemberRepository memberRepository;

    @Autowired
    private IDogRepository dogRepository;

    @Autowired
    private IDogHasHandlerRepository dogHasHandlerRepository;

    @Autowired
    private ICardRepository cardRepository;

    @Autowired
    private MockMvc mockMvc;

    private Dog dog;
    private Roles role;
    private Member member;
    private DogHasHandler dogHasHandler;

    @BeforeEach
    void before() {
        clearDb();

        role = Roles.ADMIN;

        dog = new Dog();
        dog.setName("Doggo");
        dog.setChipNumber("1234");
        dog.setOwnerName("Harry");
        dog.setOwnerAddress("Private Drive Nr. 9");
        dog = dogRepository.save(dog);

        member = new Member();
        member.setFirstName("Ciao");
        member.setLastName("Bella");
        member.setEmail("ciao@gmail.com");
        member.getRoles().add(role);
        member = memberRepository.save(member);

        dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler = dogHasHandlerRepository.save(dogHasHandler);
    }

    @Test
    void getDataById() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/{id}", dogHasHandler.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dogHasHandler.getId().toString()));
    }

    @Test
    void getDogHasHandlerByDogId() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getHandlersByDog/{id}", dog.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dogHasHandler.getId().toString()));
    }

    @Test
    void getDogHasHandlerByMemberId() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getHandlersByMember/{id}", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dogHasHandler.getId().toString()));
    }

    @Test
    void getDataByMember() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getByMember/{id}", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dog.getId().toString()));
    }

    @Test
    void getDataByDog() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getByDog/{id}", dog.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(member.getId().toString()));
    }

    @Test
    void saveDogHasHandler() throws Exception {
        Dog dog1 = new Dog();
        dog1.setName("Doggo");
        dog1.setChipNumber("1234567");
        dog1.setOwnerName("Harry");
        dog1.setOwnerAddress("Private Drive Nr. 9");
        dog1 = dogRepository.save(dog1);

        Member member1 = new Member();
        member1.setFirstName("Ciao");
        member1.setLastName("Bella");
        member1.setEmail("miao@gmail.com");
        member1.getRoles().add(role);
        member1 = memberRepository.save(member1);

        DogHasHandler dogHasHandler1 = new DogHasHandler();
        dogHasHandler1.setMemberId(member1.getId());
        dogHasHandler1.setDogId(dog1.getId());
        // save through repository not nececarry because we will test it with mockMVC

        mockMvc.perform(post(DOGHASHANDLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dogHasHandler1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.memberId").value(member1.getId().toString()));
    }

    @Test
    void deleteDataByDog() throws Exception {
        // Handler should have one Dog in his attributes
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getByDog/{id}", dog.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(member.getId().toString()));

        mockMvc.perform(delete(DOGHASHANDLER_BASE_URL + "/deleteByDog/{id}", dog.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Handler was deleted."));

        // After the Handler was deleted
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getByDog/{id}", dog.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].member.id").doesNotExist());

        dogHasHandlerRepository.deleteAll();
    }

    @Test
    void deleteDataByMember() throws Exception {
        // Handler should have one Member in his attributes
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getByMember/{id}", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dog.getId().toString()));

        mockMvc.perform(delete(DOGHASHANDLER_BASE_URL + "/deleteByMember/{id}", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Handler was deleted."));

        // After the Handler was deleted
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getByMember/{id}", member.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].member.id").doesNotExist());

        dogHasHandlerRepository.deleteAll();
    }

    @Test
    void searchDogHasHandlerByFirstNameOrLastName() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/searchDogHasHandlerByName")
                        .param("name", "Ciao"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(dogHasHandler.getId().toString()));
    }

    @Test
    void searchDogHasHandlerByDogName() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/searchDogHasHandlerByName")
                        .param("name", "Doggo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dogHasHandler.getId().toString()));
    }

    @Test
    void saveDogHasHandlerGrades() throws Exception {
        Dog dog1 = new Dog();
        dog1.setName("Doggo");
        dog1.setChipNumber("1234567");
        dog1.setOwnerName("Harry");
        dog1.setOwnerAddress("Private Drive Nr. 9");
        dog1 = dogRepository.save(dog1);

        Member member1 = new Member();
        member1.setFirstName("Ciao");
        member1.setLastName("Bella");
        member1.setEmail("miao@gmail.com");
        member1.getRoles().add(role);
        member1 = memberRepository.save(member1);

        DogHasHandler dogHasHandler1 = new DogHasHandler();
        dogHasHandler1.setMemberId(member1.getId());
        dogHasHandler1.setDogId(dog1.getId());
        Grade grade = new Grade();
        grade.setName("BH1");
        grade.setPoints((long) 100);
        grade.setExamDate(LocalDate.now());
        grade.setType(GradeType.AG2);

        Set<Grade> grades = new HashSet<>();
        grades.add(grade);
        dogHasHandler1.setGrades(grades);
        // save through repository not nececarry because we will test it with mockMVC
        dogHasHandler1.setId(null);

        mockMvc.perform(post(DOGHASHANDLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dogHasHandler1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.memberId").value(member1.getId().toString()))
                .andExpect(jsonPath("$.grades").isArray())
                .andExpect(jsonPath("$.grades.length()").value(1));
    }

    @Test
    void getAllDogHasHandlerFullList() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dogHasHandler.getId().toString()))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllDogHasHandlerEmptyList() throws Exception {
        clearDb();

        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

    }

    private void clearDb() {
        dogHasHandlerRepository.deleteAll();
        cardRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource
    void saveDogHasHandlerMultipleTimes(EntityStatus entityStatus) throws Exception {
        dogHasHandler.setEntityStatus(entityStatus);
        mockMvc.perform(post(DOGHASHANDLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dogHasHandler)))
                .andExpect(status().isOk());
    }

    private static Stream<EntityStatus> saveDogHasHandlerMultipleTimes() {
        return Stream.of(EntityStatus.INACTIVE, EntityStatus.ACTIVE, EntityStatus.DELETED);
    }


    @Test
    void saveDogHasHandlerWhichWasDeleted() throws Exception {
        dogHasHandler.setEntityStatus(EntityStatus.DELETED);
        dogHasHandlerRepository.save(dogHasHandler);

        dogHasHandler.setId(UUID.randomUUID());
        dogHasHandler.setEntityStatus(EntityStatus.ACTIVE);
        dogHasHandler.setVersion(0);

        mockMvc.perform(post(DOGHASHANDLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dogHasHandler)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource
    void saveDogHasHandlerCannotBeSavedItAlreadyExist(DogHasHandler dogHasHandler2) throws Exception {
        dogHasHandler2.setDogId(dogHasHandler.getDogId());
        dogHasHandler2.setMemberId(dogHasHandler.getMemberId());
        mockMvc.perform(post(DOGHASHANDLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dogHasHandler2)))
                .andExpect(status().is4xxClientError());
    }

    private static Stream<DogHasHandler> saveDogHasHandlerCannotBeSavedItAlreadyExist() {
        return Stream.of(setDogHasHandlerID(null), setDogHasHandlerID(UUID.randomUUID()));
    }

    @Test
    void getDogHasHandlersByHandlerIds() throws Exception {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        for (int i = 1; i <= 10; i++) {
            role = Roles.ADMIN;

            dog = new Dog();
            dog.setName("Doggo" + i);
            dog.setChipNumber("1234" + i);
            dog.setOwnerName("Harry");
            dog.setOwnerAddress("Private Drive Nr. 9" + i);
            dog = dogRepository.save(dog);

            member = new Member();
            member.setFirstName("Ciao" + i);
            member.setLastName("Bella" + i);
            member.setEmail("ciao" + i + "@gmail.com");
            member.getRoles().add(role);
            member = memberRepository.save(member);

            dogHasHandler = new DogHasHandler();
            dogHasHandler.setDogId(dog.getId());
            dogHasHandler.setMemberId(member.getId());
            dogHasHandler = dogHasHandlerRepository.save(dogHasHandler);
            parameter.add("dogHasHandlerIds", dogHasHandler.getId().toString());
        }

        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getHandlersByIds")
                        .params(parameter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @ParameterizedTest
    @MethodSource
    void saveDogHasHandlerCannotBeSavedItAlreadyExistAndIsInactive(DogHasHandler dogHasHandler2) throws Exception {
        dogHasHandler.setEntityStatus(EntityStatus.INACTIVE);
        dogHasHandlerRepository.save(dogHasHandler);
        dogHasHandler2.setDogId(dogHasHandler.getDogId());
        dogHasHandler2.setMemberId(dogHasHandler.getMemberId());
        mockMvc.perform(post(DOGHASHANDLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dogHasHandler2)))
                .andExpect(status().is4xxClientError());
    }

    private static Stream<DogHasHandler> saveDogHasHandlerCannotBeSavedItAlreadyExistAndIsInactive() {
        return Stream.of(setDogHasHandlerID(null), setDogHasHandlerID(UUID.randomUUID()));
    }

    private static DogHasHandler setDogHasHandlerID(UUID id) {
        final DogHasHandler dogHasHandler2 = new DogHasHandler();
        dogHasHandler2.setId(id);
        return dogHasHandler2;
    }

    @Test
    void getDogHasHandlerIdsByMemberId() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getDogHasHandlerIdsByMemberId/{id}",
                        dogHasHandler.getMemberId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @ParameterizedTest
    @ValueSource(strings = "wtf")
    @EmptySource
    void getDogHasHandlerIdsByMemberId(String id) throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getDogHasHandlerIdsByMemberId/{id}", id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getDogHasHandlerIdsByDogId() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getDogHasHandlerIdsByDogId/{id}",
                        dogHasHandler.getDogId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @ParameterizedTest
    @ValueSource(strings = "wtf")
    @EmptySource
    void getDogHasHandlerIdsByDogId(String id) throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/getDogHasHandlerIdsByDogId/{id}", id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void doesNotReturnMemberEmailByUnknownId() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/get-emails-by-ids")
                        .param("ids", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void doesNotReturnMemberEmailParameterIsNull() throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/get-emails-by-ids"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "XXX"})
    void doesNotReturnMemberEmailParameterIsEmptyOrBad(String id) throws Exception {
        mockMvc.perform(get(DOGHASHANDLER_BASE_URL + "/get-emails-by-ids")
                        .param("ids", id))
                .andExpect(status().isBadRequest());
    }
}
