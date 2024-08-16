package casp.web.backend.business.logic.layer.classes;

import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.entities.DogHasHandler;
import casp.web.backend.data.access.layer.entities.Grade;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.IDogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.IDogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private IDogRepository dogRepository;

    @Mock
    private IDogHasHandlerRepository dogHasHandlerRepository;

    @InjectMocks
    private DogService dogService;

    private Grade grade;
    private Dog dog;
    private DogHasHandler dogHasHandler;
    private Member member;
    private UUID id;
    private UUID randomId;

    @BeforeEach
    void setUp() {

        id = UUID.randomUUID();
        randomId = UUID.randomUUID();
        grade = new Grade();
        grade.setName("Fake1");
        grade.setPoints((long) 30);

        dog = new Dog();
        dog.setName("Socke");
        dog.setId(id);

        member = new Member();
        member.setFirstName("Luis");

        dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setMemberId(member.getId());
    }

    @Test
    void getDogByIdHappyPath() throws Exception {
        // given
        Mockito.when(dogRepository.findDogByEntityStatusAndId(EntityStatus.ACTIVE, dog.getId())).thenReturn(Optional.of(dog));

        // when
        Dog actualdog = dogService.getDogById(dog.getId());

        // then
        assertEquals(dog.getName(), actualdog.getName());
    }

    @Test
    void getDogByIdNotFound() {
        // given
        Mockito.when(dogRepository.findDogByEntityStatusAndId(EntityStatus.ACTIVE, randomId)).thenReturn(Optional.empty());

        // when + then(Exception)
        assertThrows(Exception.class, () -> dogService.getDogById(randomId));
    }

    @Test
    void getDogsByOwnerHappyPathChipnumber() {
        // given
        Mockito.when(dogRepository.findAllByChipNumber("12345")).thenReturn(List.of(dog));
        // when
        Dog actualDog = dogService.getDogsByOwner("Manuel", "Socke", "12345").iterator().next();
        // then
        assertEquals(dog.getName(), actualDog.getName());
    }

    @Test
    void getDogsByOwnerHappyPathOwnerAndName() {
        // given
        Mockito.when(dogRepository.findAllByOwnerNameAndName("Manuel", "Socke")).thenReturn(List.of(dog));
        // when
        Dog actualDog = dogService.getDogsByOwner("Manuel", "Socke", "").iterator().next();
        // then
        assertEquals(dog.getName(), actualDog.getName());
    }

}
