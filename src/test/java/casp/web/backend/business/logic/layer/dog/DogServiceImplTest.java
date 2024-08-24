package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.EuropeNetState;
import casp.web.backend.data.access.layer.repositories.DogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
        when(dogRepository.findAllByChipNumberIsNotEmptyAndEuropeNetStateIsNotAndEntityStatus(EuropeNetState.NOT_CHECKED, EntityStatus.ACTIVE)).thenReturn(Set.of(dog));

        assertThat(dogService.getDogsThatWereNotChecked()).containsExactly(dog);
    }

    @Nested
    class GetDogsByOwnerNameAndDogsNameOrChipNumber {

        private Dog dog;

        @BeforeEach
        void setUp() {
            dog = new Dog();
        }

        @Test
        void findDogByChipNumber() {
            var chipNumber = UUID.randomUUID().toString();
            when(dogRepository.findDogByChipNumberAndEntityStatus(chipNumber, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            var dogs = dogService.getDogsByOwnerNameAndDogsNameOrChipNumber(null, null, chipNumber);

            assertSame(dog, dogs.getFirst());
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @NullSource
        void findDogByOwnerNameAndName(String chipNumber) {
            var ownerName = "ownerName";
            var name = "name";
            when(dogRepository.findAllByOwnerNameAndNameAndEntityStatusOrderByNameAscOwnerNameAsc(ownerName, name, EntityStatus.ACTIVE)).thenReturn(List.of(dog));

            var dogs = dogService.getDogsByOwnerNameAndDogsNameOrChipNumber(ownerName, name, chipNumber);

            assertSame(dog, dogs.getFirst());
        }
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
