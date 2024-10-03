package casp.web.backend.data.access.layer.dog;


import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

        charlie = createDog("Charlie", EntityStatus.ACTIVE, EuropeNetState.DOG_IS_REGISTERED);
        bonsai = createDog("Bonsai", EntityStatus.ACTIVE, EuropeNetState.NOT_CHECKED);
        createDog("INACTIVE", EntityStatus.INACTIVE, EuropeNetState.NOT_CHECKED);
    }

    @Test
    void findAllByEuropeNetStateNotChecked() {
        assertThat(dogRepository.findAllByEuropeNetStateNotChecked(Pageable.unpaged())).containsExactly(bonsai);
    }

    private Dog createDog(final String name, final EntityStatus entityStatus, final EuropeNetState europeNetState) {
        var dog = new Dog();
        dog.setEntityStatus(entityStatus);
        dog.setEuropeNetState(europeNetState);
        dog.setName(name);
        dog.setChipNumber(UUID.randomUUID().toString());
        dog.setOwnerName("John Doe");
        return dogRepository.save(dog);
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
