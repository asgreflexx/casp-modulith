package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.data.access.layer.repositories.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.DogRepository;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.MongoTransactionManager;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogHasHandlerServiceImplTest {
    @Mock
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Mock
    private DogRepository dogRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MongoTransactionManager mongoTransactionManager;

    @InjectMocks
    private DogHasHandlerServiceImpl dogHasHandlerService;

    private Dog dog;
    private UUID dogId;
    private Member member;
    private UUID memberId;
    private DogHasHandler dogHasHandler;

    @BeforeEach
    void setUp() {
        dog = new Dog();
        dogId = dog.getId();
        member = new Member();
        memberId = member.getId();
        dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dogId);
        dogHasHandler.setMemberId(memberId);
    }

    @Test
    void saveDogHasHandler() {
        when(dogRepository.findDogByIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));
        when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

        dogHasHandlerService.saveDogHasHandler(dogHasHandler);

        var captor = ArgumentCaptor.forClass(DogHasHandler.class);
        verify(dogHasHandlerRepository).save(captor.capture());
        var actualDogHasHandler = captor.getValue();
        assertSame(member, actualDogHasHandler.getMember());
        assertSame(dog, actualDogHasHandler.getDog());
    }

    @Test
    void deleteDogHasHandlerByMemberId() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, memberId))
                .thenReturn(Set.of(dogHasHandler));

        dogHasHandlerService.deleteDogHasHandlerByMemberId(memberId);

        assertSame(EntityStatus.DELETED, dogHasHandler.getEntityStatus());
    }

    @Test
    void deleteDogHasHandlerByDogId() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, dogId))
                .thenReturn(Set.of(dogHasHandler));

        dogHasHandlerService.deleteDogHasHandlerByDogId(dogId);

        assertSame(EntityStatus.DELETED, dogHasHandler.getEntityStatus());
    }

    @Test
    void getDogsByMemberId() {
        when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE))
                .thenReturn(Optional.of(member));
        when(dogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, memberId))
                .thenReturn(Set.of(dogHasHandler));
        when(dogRepository.findDogByIdAndEntityStatus(dogHasHandler.getDogId(), EntityStatus.ACTIVE))
                .thenReturn(Optional.of(dog));

        assertThat(dogHasHandlerService.getDogsByMemberId(memberId)).containsExactly(dog);
    }

    @Test
    void getMembersByDogId() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, dogId))
                .thenReturn(Set.of(dogHasHandler));
        when(dogRepository.findDogByIdAndEntityStatus(dogId, EntityStatus.ACTIVE))
                .thenReturn(Optional.of(dog));
        when(memberRepository.findByIdAndEntityStatus(dogHasHandler.getMemberId(), EntityStatus.ACTIVE))
                .thenReturn(Optional.of(member));

        assertThat(dogHasHandlerService.getMembersByDogId(dogId)).containsExactly(member);
    }

    @Test
    void getHandlersByMemberId() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, memberId))
                .thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getHandlersByMemberId(memberId)).containsExactly(dogHasHandler);
    }

    @Test
    void getHandlersByDogId() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, dogId))
                .thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getHandlersByDogId(dogId)).containsExactly(dogHasHandler);
    }

    @Test
    void searchDogHasHandlerByFirstNameOrLastNameOrDogName() {
        dog.setName("casp");
        member.setFirstName(dog.getName());
        member.setLastName(dog.getName());
        when(dogHasHandlerRepository.findAllByMemberNameOrDogName(dog.getName())).thenReturn(Set.of(dogHasHandler));


        assertThat(dogHasHandlerService.searchDogHasHandlerByFirstNameOrLastNameOrDogName(dog.getName()))
                .containsExactly(dogHasHandler);
    }

    @Test
    void getAllDogHasHandler() {
        when(dogHasHandlerRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getAllDogHasHandler()).containsExactly(dogHasHandler);
    }

    @Test
    void getDogHasHandlersByIds() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, Set.of(dogHasHandler.getId()))).thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getDogHasHandlersByIds(Set.of(dogHasHandler.getId()))).containsExactly(dogHasHandler);
    }

    @Test
    void getDogHasHandlerIdsByMemberId() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, memberId)).thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getDogHasHandlerIdsByMemberId(memberId)).containsExactly(dogHasHandler.getId());
    }

    @Test
    void getDogHasHandlerIdsByDogId() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, dogId)).thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getDogHasHandlerIdsByDogId(dogId)).containsExactly(dogHasHandler.getId());
    }

    @Test
    void getMembersEmailByIds() {
        member.setEmail("XXXXXXXXXXXXX");
        when(dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, Set.of(dogHasHandler.getId()))).thenReturn(Set.of(dogHasHandler));
        when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

        assertThat(dogHasHandlerService.getMembersEmailByIds(Set.of(dogHasHandler.getId()))).containsExactly(member.getEmail());
    }

    @Nested
    class GetDogHasHandlerById {
        @Test
        void handlerExist() {
            when(dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandler.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandler));

            assertSame(dogHasHandler, dogHasHandlerService.getDogHasHandlerById(dogHasHandler.getId()));
        }

        @Test
        void handlerDoesNotExist() {
            var dogHasHandlerId = dogHasHandler.getId();
            when(dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandlerId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> dogHasHandlerService.getDogHasHandlerById(dogHasHandlerId));
        }
    }
}
