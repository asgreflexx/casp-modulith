package casp.web.backend.dog;

import casp.web.backend.business.logic.layer.event.types.CourseService;
import casp.web.backend.business.logic.layer.event.types.SpaceDto;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.dog.data.Dog;
import casp.web.backend.dog.data.DogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.dog.DogMapper.DOG_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogServiceImplTest {
    @Mock
    private DogRepository dogRepository;
    @Mock
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @Mock
    private DogHasHandlerService dogHasHandlerService;
    @Mock
    private CourseService courseService;

    @Captor
    private ArgumentCaptor<Dog> dogCaptor;

    @InjectMocks
    private DogServiceImpl dogService;
    private Dog dog;
    private DogDto dogDto;

    @BeforeEach
    void setUp() {
        dog = TestFixture.createDog();
        dogDto = DOG_MAPPER.toTarget(dog);
    }

    @Test
    void getDogs() {
        when(dogRepository.findAllByEntityStatus(EntityStatus.ACTIVE, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(dog)));

        var dogPage = dogService.getDogs(Pageable.unpaged());

        assertThat(dogPage).containsExactly(dogDto);
    }

    @Test
    void getDogByChipNumber() {
        var chipNumber = UUID.randomUUID().toString();
        when(dogRepository.findOneByChipNumberAndEntityStatus(chipNumber, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

        assertThat(dogService.getDogByChipNumber(chipNumber)).usingRecursiveComparison().isEqualTo(Optional.of(dogDto));
    }

    @Test
    void getDogsByNameOrOwnerName() {
        when(dogRepository.findAllByNameOrOwnerName(dog.getName(), dog.getOwnerName(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(dog)));

        var dogPage = dogService.getDogsByNameOrOwnerName(dog.getName(), dog.getOwnerName(), Pageable.unpaged());

        assertThat(dogPage).containsExactly(dogDto);
    }

    @Nested
    class SaveDog {
        @Test
        void createNewDog() {
            when(dogRepository.setMetadataAndSave(dog)).thenAnswer(i -> i.getArgument(0));

            var actualDogDto = dogService.saveDog(dogDto);

            verify(courseService).getSpacesByDogHasHandlers(Set.of());
            assertThat(actualDogDto).usingRecursiveAssertion().isEqualTo(dogDto);
        }

        @Test
        void updateExistingDog() {
            when(dogRepository.setMetadataAndSave(dog)).thenAnswer(i -> i.getArgument(0));

            dogService.saveDog(dogDto);

            verify(courseService).getSpacesByDogHasHandlers(Set.of());
            verify(dogRepository).setMetadataAndSave(dogCaptor.capture());
            assertThat(dogCaptor.getValue()).usingRecursiveAssertion().isEqualTo(dog);
        }
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
            assertThat(dogService.getDogsThatWereNotChecked(Pageable.unpaged())).containsExactly(DOG_MAPPER.toTarget(dog));
        }

        @Test
        void pageableIsNotNull() {
            assertThat(dogService.getDogsThatWereNotChecked(Pageable.unpaged())).containsExactly(DOG_MAPPER.toTarget(dog));
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
            when(dogRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            dogService.deleteDogById(id);

            verify(dog).setEntityStatus(EntityStatus.DELETED);
            verify(dogHasHandlerService).deleteDogHasHandlersByDogId(id);
        }

        @Test
        void dogWasNotFound() {
            when(dogRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> dogService.deleteDogById(id));
        }
    }

    @Nested
    class GetDogById {

        @Test
        void dogWasFound() {
            when(dogRepository.findOneByIdAndEntityStatus(dog.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            var result = dogService.getDogById(dog.getId());

            assertEquals(DOG_MAPPER.toTarget(dog), result);
        }

        @Test
        void dogWasNotFound() {
            var dogId = dog.getId();
            when(dogRepository.findOneByIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> dogService.getDogById(dogId));
        }

        @Test
        void mapDogHasHandler() {
            var dogHasHandlerReference = mockDogHasHandler();
            when(dogRepository.findOneByIdAndEntityStatus(dog.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            var result = dogService.getDogById(dog.getId());

            assertThat(result.getDogHasHandlerSet()).singleElement().usingRecursiveAssertion().isEqualTo(DOG_MAPPER.toDogHasHandler(dogHasHandlerReference));
        }

        @Test
        void mapSpaces() {
            var dogHasHandlerReference = mockDogHasHandler();
            var spaceDto = mock(SpaceDto.class);
            when(dogRepository.findOneByIdAndEntityStatus(dog.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));
            when(courseService.getSpacesByDogHasHandlers(Set.of(dogHasHandlerReference))).thenReturn(Set.of(spaceDto));

            var memberDto = dogService.getDogById(dog.getId());

            assertThat(memberDto.getSpaces())
                    .singleElement()
                    .isEqualTo(spaceDto);

        }

        private DogHasHandlerReference mockDogHasHandler() {
            var dogHasHandlerReference = mock(DogHasHandlerReference.class, Answers.RETURNS_DEEP_STUBS);
            when(dogHasHandlerReference.getId()).thenReturn(UUID.randomUUID());
            when(dogHasHandlerReference.getMember().getId()).thenReturn(UUID.randomUUID());
            when(dogHasHandlerReference.getMember().getFirstName()).thenReturn("Bonsai");
            when(dogHasHandlerReference.getMember().getLastName()).thenReturn("Yasmin");
            when(dogHasHandlerReferenceRepository.findAllByDogId(dog.getId())).thenReturn(Set.of(dogHasHandlerReference));
            return dogHasHandlerReference;
        }
    }
}
