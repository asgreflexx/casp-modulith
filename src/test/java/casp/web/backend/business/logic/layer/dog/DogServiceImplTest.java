package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.DogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogServiceImplTest {
    @Mock
    private DogRepository dogRepository;

    @Mock
    private DogHasHandlerService dogHasHandlerService;

    @InjectMocks
    private DogServiceImpl dogService;

    @Test
    void saveDog() {
        var dog = new Dog();
        dogService.saveDog(dog);

        verify(dogRepository).save(dog);
    }

    @Test
    void getDogs() {
        var pageable = Pageable.ofSize(10);
        when(dogRepository.findAllByEntityStatus(EntityStatus.ACTIVE, pageable))
                .thenReturn(Page.empty());

        var dogPage = dogService.getDogs(pageable);

        assertThat(dogPage.stream().toList()).isEmpty();
    }

    @Test
    void getDogsThatWereNotChecked() {
        var dog = new Dog();
        dog.setChipNumber("1234");
        when(dogRepository.findAllByEuropeNetStateNotChecked()).thenReturn(Set.of(dog));

        assertThat(dogService.getDogsThatWereNotChecked()).containsExactly(dog);
    }

    @Test
    void getDogsByOwnerNameAndDogsNameOrChipNumber() {
        var dog = new Dog();
        var chipNumber = UUID.randomUUID().toString();
        when(dogRepository.findAllByChipNumberOrDogNameOrOwnerName(chipNumber, null, null)).thenReturn(List.of(dog));

        assertThat(dogService.getDogsByOwnerNameAndDogsNameOrChipNumber(chipNumber, null, null))
                .containsExactly(dog);
    }


    @Nested
    class DeleteDogById {

        private Dog dog;
        private UUID id;

        @BeforeEach
        void setUp() {
            dog = spy(new Dog());
            id = UUID.randomUUID();
        }

        @Test
        void dogWasFound() {
            when(dogRepository.findDogByIdAndEntityStatusIsNot(id, EntityStatus.DELETED)).thenReturn(Optional.of(dog));

            dogService.deleteDogById(id);

            verify(dog).setEntityStatus(EntityStatus.DELETED);
            verify(dogHasHandlerService).deleteDogHasHandlerByDogId(id);
        }

        @Test
        void dogWasNotFound() {
            when(dogRepository.findDogByIdAndEntityStatusIsNot(id, EntityStatus.DELETED)).thenReturn(Optional.empty());

            dogService.deleteDogById(id);

            verifyNoInteractions(dogHasHandlerService, dog);
        }
    }

    @Nested
    class GetDogById {

        private UUID id;

        @BeforeEach
        void setUp() {
            id = UUID.randomUUID();
        }

        @Test
        void dogWasFound() {
            var dog = new Dog();
            when(dogRepository.findDogByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            var result = dogService.getDogById(id);

            assertSame(dog, result);
        }

        @Test
        void dogWasNotFound() {
            when(dogRepository.findDogByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> dogService.getDogById(id));
        }
    }
}
