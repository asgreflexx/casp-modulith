package casp.web.backend.presentation.layer;

import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.repositories.ICardRepository;
import casp.web.backend.data.access.layer.repositories.IDogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.IDogRepository;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static casp.web.backend.presentation.layer.ObjectStringMapper.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DogRestControllerTest {

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

    @BeforeEach
    void before() {
        dogHasHandlerRepository.deleteAll();
        cardRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();

        dog = new Dog();
        dog.setName("Doggo");
        dog.setChipNumber("1234");
        dog.setOwnerName("Harry");
        dog.setOwnerAddress("Private Drive Nr. 9");
        dog = dogRepository.save(dog);

        Dog dog2 = new Dog();
        dog2.setName("Doggo2");
        dog2.setChipNumber("12345");
        dog2.setOwnerName("Harry2");
        dog2.setOwnerAddress("Private Drive Nr. 29");
        dogRepository.save(dog2);

    }

    @Test
    void getDogById() throws Exception {
        mockMvc.perform(get("/dog/{id}", dog.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dog.getId().toString()))
                .andExpect(jsonPath("$.name").value("Doggo"));
    }

    @Test
    void getDogsByOwner() throws Exception {
        mockMvc.perform(get("/dog/getByOwner")
                        .param("ownerName", "Harry")
                        .param("name", "Doggo")
                        .param("chipNumber", "1234"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dog.getId().toString()))
                .andExpect(jsonPath("$[0].ownerName").value("Harry"));
    }

    @Test
    void getDogsByOwnerWithoutParamOwnerNameAndName() throws Exception {
        mockMvc.perform(get("/dog/getByOwner")
                        .param("ownerName", "")
                        .param("name", "")
                        .param("chipNumber", "1234"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dog.getId().toString()))
                .andExpect(jsonPath("$[0].ownerName").value("Harry"));
    }

    @Test
    void getDogsByOwnerWithoutParamChipNumber() throws Exception {
        mockMvc.perform(get("/dog/getByOwner")
                        .param("ownerName", "Harry")
                        .param("name", "Doggo")
                        .param("chipNumber", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dog.getId().toString()))
                .andExpect(jsonPath("$[0].ownerName").value("Harry"));
    }

    @Test
    void getDogs() throws Exception {
        mockMvc.perform(get("/dog/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(dog.getId().toString()))
                .andExpect(jsonPath("$[0].ownerName").value("Harry"));
    }

    @Test
    void postData() throws Exception {
        Dog dog1 = new Dog();
        dog1.setName("Doggo1");
        dog1.setChipNumber("1234");
        dog1.setOwnerName("Harry1");
        dog1.setOwnerAddress("Private Drive Nr. 19");
        // save through repository not nececarry because we will test it with mockMVC

        mockMvc.perform(post("/dog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dog1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerName").value(dog1.getOwnerName()));
    }

    @Test
    void deleteDataById() throws Exception {
        mockMvc.perform(delete("/dog/{id}", dog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Dog was deleted."));

        mockMvc.perform(get("/dog/{id}", dog.getId()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").value("Dog not found."));
    }
}
