package casp.web.backend.data.access.layer.dog;


import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class DogCustomRepositoryImplTest {
    @Autowired
    private DogRepository dogRepository;

    private Dog bonsai;
    private Dog charlie;

    @BeforeEach
    void setUp() {
        dogRepository.deleteAll();

        charlie = createDog("Charlie", EntityStatus.ACTIVE, EuropeNetState.DOG_IS_REGISTERED, UUID.randomUUID().toString());
        bonsai = createDog("Bonsai", EntityStatus.ACTIVE, EuropeNetState.NOT_CHECKED, UUID.randomUUID().toString());
        createDog("INACTIVE", EntityStatus.INACTIVE, EuropeNetState.NOT_CHECKED, UUID.randomUUID().toString());

    }

    private Dog createDog(final String name, final EntityStatus entityStatus, final EuropeNetState europeNetState, final String chipNumber) {
        var dog = new Dog();
        dog.setEntityStatus(entityStatus);
        dog.setEuropeNetState(europeNetState);
        dog.setName(name);
        dog.setChipNumber(chipNumber);
        dog.setOwnerName("John Doe");
        return dogRepository.save(dog);
    }

    @Nested
    class FindAllByEuropeNetStateNotChecked {
        @ParameterizedTest
        @NullAndEmptySource
        void findAllByEuropeNetStateNotChecked(String chipNumber) {
            createDog("BAD_CHIP_NUMBER", EntityStatus.ACTIVE, EuropeNetState.NOT_CHECKED, chipNumber);
            assertThat(dogRepository.findAllByEuropeNetStateNotChecked(Pageable.unpaged())).containsExactly(bonsai);
        }

        @Test
        void notRegistered() {
            createDog("DOG_NOT_REGISTERED", EntityStatus.ACTIVE, EuropeNetState.DOG_NOT_REGISTERED, UUID.randomUUID().toString());
            assertThat(dogRepository.findAllByEuropeNetStateNotChecked(Pageable.unpaged())).containsExactly(bonsai);
        }
    }

    @Nested
    class FindAllByChipNumberOrDogNameOrOwnerName {
        @Test
        void findDogByChipNumber() {
            assertThat(dogRepository.findAllByChipNumberOrDogNameOrOwnerName(bonsai.getChipNumber(), null, null))
                    .containsExactly(bonsai);
        }

        @Test
        void findDogByDogName() {
            assertThat(dogRepository.findAllByChipNumberOrDogNameOrOwnerName(null, bonsai.getName(), null))
                    .containsExactly(bonsai);
        }

        @Test
        void findDogByDogNameAndOwnerName() {
            assertThat(dogRepository.findAllByChipNumberOrDogNameOrOwnerName(null, bonsai.getName(), bonsai.getOwnerName()))
                    .containsExactly(bonsai);
        }

        @Test
        void findDogsByOwnerName() {
            assertThat(dogRepository.findAllByChipNumberOrDogNameOrOwnerName(null, null, bonsai.getOwnerName()))
                    .containsExactly(bonsai, charlie);
        }

        @Test
        void findDogsWithoutParameters() {
            assertThat(dogRepository.findAllByChipNumberOrDogNameOrOwnerName(null, null, null))
                    .containsExactly(bonsai, charlie);
        }
    }
}
