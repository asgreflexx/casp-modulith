package casp.web.backend.business.logic.layer.classes;

import casp.web.backend.business.logic.layer.interfaces.IMemberService;
import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.entities.DogHasHandler;
import casp.web.backend.data.access.layer.entities.Grade;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.IDogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.IDogRepository;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DogHasHandlerServiceTest {

    @Mock
    private IDogHasHandlerRepository dogHasHandlerRepository;

    @Mock
    private IDogRepository dogRepository;

    @Mock
    private IMemberRepository memberRepository;
    @Mock
    private IMemberService memberService;

    @InjectMocks
    private DogHasHandlerService dogHasHandlerService;

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
        dog.setId(UUID.randomUUID());

        member = new Member();
        member.setFirstName("Luis");
        member.setEmail("Luis@mail.com");

        dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setMemberId(member.getId());
    }

    @Test
    void getDogHasHandlerById() {
        // given
        Mockito.when(dogHasHandlerRepository.findById(dogHasHandler.getId())).thenReturn(Optional.of(dogHasHandler));

        // when
        DogHasHandler actualdogHasHandler = dogHasHandlerService.getDogHasHandlerById(dogHasHandler.getId());

        // then
        assertEquals(dogHasHandler.getDogId(), actualdogHasHandler.getDogId());
    }

    @Test
    void getDogHasHandlerByIdNotFound() {
        // given
        Mockito.when(dogHasHandlerRepository.findById(randomId)).thenReturn(Optional.empty());

        // when + then(Exception)
        assertThrows(Exception.class, () -> dogHasHandlerService.getDogHasHandlerById(randomId));
    }

    @Test
    void getHandlerByMember() {
        // given
        Mockito.when(memberRepository.findById(id)).thenReturn(Optional.of(member));
        Mockito.when(dogHasHandlerRepository.findAllByMemberId(member.getId())).thenReturn(List.of(dogHasHandler));
        // when
        DogHasHandler actualDogHasHandler = dogHasHandlerService.getHandlerByMemberId(id).iterator().next();
        // then
        assertEquals(dogHasHandler.getDogId(), actualDogHasHandler.getDogId());
    }

    @Test
    void getHandlerByDog() {
        // given
        Mockito.when(dogRepository.findById(id)).thenReturn(Optional.of(dog));
        Mockito.when(dogHasHandlerRepository.findAllByDogId(dog.getId())).thenReturn(List.of(dogHasHandler));
        // when
        DogHasHandler actualDogHasHandler = dogHasHandlerService.getHandlerByDogId(id).iterator().next();
        // then
        assertEquals(dogHasHandler.getDogId(), actualDogHasHandler.getDogId());
    }

    @Test
    void saveNewDogHasHandler() {
        Mockito.when(dogHasHandlerRepository.findOneByMemberIdAndDogIdAndEntityStatusNotLike(member.getId(), dog.getId(), EntityStatus.DELETED)).thenReturn(Optional.empty());
        Mockito.when(dogHasHandlerRepository.save(dogHasHandler)).thenReturn(dogHasHandler);

        dogHasHandlerService.saveDogHasHandler(dogHasHandler);

        final InOrder order = Mockito.inOrder(dogHasHandlerRepository);
        order.verify(dogHasHandlerRepository).findOneByMemberIdAndDogIdAndEntityStatusNotLike(member.getId(), dog.getId(), EntityStatus.DELETED);
        order.verify(dogHasHandlerRepository).save(dogHasHandler);
    }

    @Test
    void saveNewDogHasHandlerButOtherAlreadyExist() {
        final DogHasHandler dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setMemberId(member.getId());

        Mockito.when(dogHasHandlerRepository.findOneByMemberIdAndDogIdAndEntityStatusNotLike(member.getId(), dog.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(dogHasHandler));

        assertThrows(IllegalArgumentException.class, () -> dogHasHandlerService.saveDogHasHandler(this.dogHasHandler));

    }

    @Test
    void getDogHasHandlerIdsByMemberId() {
        final DogHasHandler dogHasHandler = new DogHasHandler();
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler.setDogId(dog.getId());
        Mockito.when(dogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, member.getId()))
                .thenReturn(List.of(dogHasHandler));

        final Set<UUID> dogHasHandlerId = dogHasHandlerService.getDogHasHandlerIdsByMemberId(member.getId());

        assertEquals(1, dogHasHandlerId.size());
        assertEquals(dogHasHandlerId.iterator().next(), dogHasHandler.getId());
    }

    @Test
    void getDogHasHandlerIdsByDogId() {

        final DogHasHandler dogHasHandler = new DogHasHandler();
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler.setDogId(dog.getId());
        Mockito.when(dogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, dog.getId()))
                .thenReturn(List.of(dogHasHandler));

        final Set<UUID> dogHasHandlerId = dogHasHandlerService.getDogHasHandlerIdsByDogId(dog.getId());

        assertEquals(1, dogHasHandlerId.size());
        assertEquals(dogHasHandlerId.iterator().next(), dogHasHandler.getId());
    }

    @Test
    void returnMemberEmail() {
        final DogHasHandler dogHasHandler = new DogHasHandler();
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler.setDogId(dog.getId());

        Mockito.when(dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, List.of(dogHasHandler.getId()))).thenReturn(List.of(dogHasHandler));
        Mockito.when(memberService.getMembersEmailByIds(Set.of(member.getId()))).thenReturn(Set.of(member.getEmail()));

        Set<String> emails = dogHasHandlerService.getMembersEmailByIds(Set.of(dogHasHandler.getId()));

        assertEquals(1, emails.size());
        assertEquals(member.getEmail(), emails.iterator().next());
    }
}
