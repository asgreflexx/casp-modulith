package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
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
    void getDogsByOwnerNameAndDogsNameOrChipNumber() {
        var dog = new Dog();
        var chipNumber = UUID.randomUUID().toString();
        when(dogRepository.findAllByChipNumberOrDogNameOrOwnerName(chipNumber, null, null)).thenReturn(List.of(dog));

        assertThat(dogService.getDogsByOwnerNameAndDogsNameOrChipNumber(chipNumber, null, null))
                .containsExactly(dog);
    }

    @Nested
    class GetDogsThatWereNotChecked {
        private Dog dog;

        @BeforeEach
        void setUp() {
            dog = new Dog();
            dog.setChipNumber("1234");
            var dogPage = new PageImpl<>(List.of(dog));
            when(dogRepository.findAllByEuropeNetStateNotChecked(Pageable.unpaged())).thenReturn(dogPage);
        }

        @Test
        void pageableIsNull() {
            assertThat(dogService.getDogsThatWereNotChecked(null)).containsExactly(dog);
        }

        @Test
        void pageableIsNotNull() {
            assertThat(dogService.getDogsThatWereNotChecked(Pageable.unpaged())).containsExactly(dog);
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
            when(dogRepository.findDogByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            dogService.deleteDogById(id);

            verify(dog).setEntityStatus(EntityStatus.DELETED);
            verify(dogHasHandlerService).deleteDogHasHandlersByDogId(id);
        }

        @Test
        void dogWasNotFound() {
            when(dogRepository.findDogByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> dogService.deleteDogById(id));
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
