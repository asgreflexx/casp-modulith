package casp.web.backend.dog.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest
class DogCustomRepositoryImplTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    private static final String FAMILY_NAME = "Doe";
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerRepository;
    @Autowired
    private MemberReferenceRepository memberRepository;

    private Dog bonsai;
    private Dog charlie;

    @BeforeEach
    void setUp() {
        dogHasHandlerRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();

        charlie = createDog("Charlie", "Charlie " + FAMILY_NAME, EntityStatus.ACTIVE, EuropeNetState.DOG_IS_REGISTERED, UUID.randomUUID().toString());
        bonsai = createDog("Bonsai", "Bonsai " + FAMILY_NAME, EntityStatus.ACTIVE, EuropeNetState.NOT_CHECKED, UUID.randomUUID().toString());
        createDog("INACTIVE", "Inactive " + FAMILY_NAME, EntityStatus.INACTIVE, EuropeNetState.NOT_CHECKED, UUID.randomUUID().toString());
    }

    private Dog createDog(String name, String ownerName, EntityStatus entityStatus, EuropeNetState europeNetState, String chipNumber) {
        var dog = new Dog();
        dog.setEntityStatus(entityStatus);
        dog.setEuropeNetState(europeNetState);
        dog.setName(name);
        dog.setChipNumber(chipNumber);
        dog.setOwnerName(ownerName);
        return dogRepository.save(dog);
    }

    @Nested
    class FindAllByEuropeNetStateNotChecked {
        @ParameterizedTest
        @NullAndEmptySource
        void findAllByEuropeNetStateNotChecked(String chipNumber) {
            createDog("BAD_CHIP_NUMBER", bonsai.getOwnerName(), EntityStatus.ACTIVE, EuropeNetState.NOT_CHECKED, chipNumber);
            assertThat(dogRepository.findAllByEuropeNetStateNotChecked(Pageable.unpaged())).containsExactly(bonsai);
        }

        @Test
        void notRegistered() {
            createDog("DOG_NOT_REGISTERED", bonsai.getOwnerName(), EntityStatus.ACTIVE, EuropeNetState.DOG_NOT_REGISTERED, UUID.randomUUID().toString());
            assertThat(dogRepository.findAllByEuropeNetStateNotChecked(Pageable.unpaged())).containsExactly(bonsai);
        }
    }

    @Nested
    class FindAllByValue {
        @Test
        void chipNumber() {
            assertThat(dogRepository.findAllByValue(bonsai.getChipNumber(), Pageable.unpaged())).containsExactly(bonsai);
        }

        @Test
        void name() {
            assertThat(dogRepository.findAllByValue(bonsai.getName(), Pageable.unpaged())).containsExactly(bonsai);
        }

        @Test
        void ownerName() {
            assertThat(dogRepository.findAllByValue(FAMILY_NAME, Pageable.unpaged())).containsExactlyInAnyOrder(bonsai, charlie);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void value(String value) {
            assertThat(dogRepository.findAllByValue(value, Pageable.unpaged())).containsExactlyInAnyOrder(bonsai, charlie);
        }
    }
}
