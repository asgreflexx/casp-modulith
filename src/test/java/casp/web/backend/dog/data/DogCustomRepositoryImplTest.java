package casp.web.backend.dog.data;


import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class DogCustomRepositoryImplTest {
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
    class FindAllByNotMemberId {

        private MemberReference memberReference;

        @BeforeEach
        void setUp() {
            var charlieReference = new DogReference();
            charlieReference.setId(charlie.getId());
            charlieReference.setName(charlie.getName());
            memberReference = new MemberReference();
            memberReference.setFirstName("firstName");
            memberReference.setLastName("lastName");
            memberReference.setEmail("email@email.com");
            memberReference = memberRepository.save(memberReference);
            var dogHasHandler = new DogHasHandlerReference();
            dogHasHandler.setDog(charlieReference);
            dogHasHandler.setMember(memberReference);
            dogHasHandlerRepository.save(dogHasHandler);
        }

        @Test
        void byMemberIdAndName() {
            assertThat(dogRepository.findAllByNotMemberId(memberReference.getId(), charlie.getName(), Pageable.unpaged())).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"Bonsai", FAMILY_NAME})
        void byMemberIdAndOwnerName(String name) {
            assertThat(dogRepository.findAllByNotMemberId(memberReference.getId(), name, Pageable.unpaged())).containsExactly(bonsai);
        }
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
    class FindAllByChipNumberOrDogNameOrOwnerName {

        @Test
        void findDogByDogName() {
            assertThat(dogRepository.findAllByNameOrOwnerName(bonsai.getName(), null, Pageable.unpaged()))
                    .containsExactly(bonsai);
        }

        @Test
        void findDogByDogNameAndOwnerName() {
            assertThat(dogRepository.findAllByNameOrOwnerName(bonsai.getName(), bonsai.getOwnerName(), Pageable.unpaged()))
                    .containsExactly(bonsai);
        }

        @Test
        void findDogsByOwnerName() {
            assertThat(dogRepository.findAllByNameOrOwnerName(null, bonsai.getOwnerName(), Pageable.unpaged()).getContent())
                    .containsExactlyInAnyOrder(bonsai);
        }

        @Test
        void findDogsWithoutParameters() {
            assertThat(dogRepository.findAllByNameOrOwnerName(null, null, Pageable.unpaged()).stream())
                    .containsExactlyInAnyOrder(bonsai, charlie);
        }
    }

    @Nested
    class FindAllByValue {
        @Test
        void chipNumber() {
            assertThat(dogRepository.findAllByValue(bonsai.getChipNumber(), Pageable.unpaged()))
                    .containsExactly(bonsai);
        }

        @Test
        void name() {
            assertThat(dogRepository.findAllByValue(bonsai.getName(), Pageable.unpaged()))
                    .containsExactly(bonsai);
        }

        @Test
        void ownerName() {
            assertThat(dogRepository.findAllByValue(FAMILY_NAME, Pageable.unpaged()))
                    .containsExactlyInAnyOrder(bonsai, charlie);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void value(String value) {
            assertThat(dogRepository.findAllByValue(value, Pageable.unpaged()))
                    .containsExactlyInAnyOrder(bonsai, charlie);
        }
    }
}
