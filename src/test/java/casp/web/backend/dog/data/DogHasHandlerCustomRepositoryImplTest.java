package casp.web.backend.dog.data;


import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.DogReferenceRepository;
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
class DogHasHandlerCustomRepositoryImplTest {

    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private DogReferenceRepository dogReferenceRepository;
    @Autowired
    private MemberReferenceRepository memberRepository;

    private DogHasHandler activeDogHasHandler;
    private DogHasHandler inactiveDogHasHandler;
    private UUID dogId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        dogHasHandlerRepository.deleteAll();
        dogReferenceRepository.deleteAll();
        memberRepository.deleteAll();
        var dog = dogReferenceRepository.save(ReferenceTestFixture.createDogReference());
        dogId = dog.getId();
        var member = memberRepository.save(ReferenceTestFixture.createMemberReference());
        memberId = member.getId();

        activeDogHasHandler = createDogHasHandler(EntityStatus.ACTIVE, member, dog);
        inactiveDogHasHandler = createDogHasHandler(EntityStatus.INACTIVE, member, dog);
    }

    @Test
    void findAllByDogIdAndNotDeleted() {
        var dogHasHandlers = dogHasHandlerRepository.findAllByDogIdAndNotDeleted(dogId);

        assertThat(dogHasHandlers).containsExactlyInAnyOrder(activeDogHasHandler, inactiveDogHasHandler);
    }

    @Test
    void findAllByMemberIdAndNotDeleted() {
        var dogHasHandlers = dogHasHandlerRepository.findAllByMemberIdAndNotDeleted(memberId);

        assertThat(dogHasHandlers).containsExactlyInAnyOrder(activeDogHasHandler, inactiveDogHasHandler);
    }

    @Test
    void findAllByMemberIdAndEntityStatus() {
        var dogHasHandlers = dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE);

        assertThat(dogHasHandlers).containsExactly(activeDogHasHandler);
    }

    private DogHasHandler createDogHasHandler(EntityStatus entityStatus, MemberReference member, DogReference dog) {
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setEntityStatus(entityStatus);
        dogHasHandler.setDog(dog);
        dogHasHandler.setMember(member);
        return dogHasHandlerRepository.save(dogHasHandler);
    }

    @Test
    void findByDogIdAndMemberId() {
        var dogHasHandlerOptional = dogHasHandlerRepository.findByDogIdAndMemberId(dogId, memberId);

        assertThat(dogHasHandlerOptional)
                .isPresent()
                .hasValue(activeDogHasHandler);
    }

    @Nested
    class FindAllByValue {

        private DogHasHandler activeDogHasHandler2;

        @BeforeEach
        void setUp() {
            activeDogHasHandler2 = new DogHasHandler();
            var dog = ReferenceTestFixture.createDogReference("Robert");
            dog.setChipNumber("123456789");
            activeDogHasHandler2.setDog(dogReferenceRepository.save(dog));
            activeDogHasHandler2.setMember(memberRepository.save(ReferenceTestFixture.createMemberReference("Maximilian", "Mustermann")));
            dogHasHandlerRepository.save(activeDogHasHandler2);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void nameIsEmptyOrNull(String value) {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(value, Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactlyInAnyOrder(activeDogHasHandler, activeDogHasHandler2);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Rob", "Max", "Must", "12345"})
        void findByShortValue(String value) {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(value, Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler2);
        }

        @Test
        void findByDogNameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(activeDogHasHandler.getDog().getName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler);
        }

        @Test
        void findByMemberFirstnameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(activeDogHasHandler.getMember().getFirstName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler);
        }

        @Test
        void findByMemberLastnameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(activeDogHasHandler.getMember().getLastName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler);
        }
    }
}
