package casp.web.backend.dog.data;


import casp.web.backend.TestFixture;
import casp.web.backend.common.enums.EntityStatus;
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
    private DogRepository dogRepository;
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
        dogRepository.deleteAll();
        memberRepository.deleteAll();

        createDogAndReturnItsId();
        var member = memberRepository.save(TestFixture.createMemberReference());
        memberId = member.getId();

        activeDogHasHandler = createDogHasHandler(EntityStatus.ACTIVE, member);
        inactiveDogHasHandler = createDogHasHandler(EntityStatus.INACTIVE, member);
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

    private DogHasHandler createDogHasHandler(EntityStatus entityStatus, MemberReference member) {
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setEntityStatus(entityStatus);
        dogReferenceRepository.findById(dogId).ifPresent(dogHasHandler::setDog);
        dogHasHandler.setMember(member);
        return dogHasHandlerRepository.save(dogHasHandler);
    }

    private void createDogAndReturnItsId() {
        var dog = TestFixture.createDog();
        dog.setName("Bonsai");
        dogRepository.save(dog);
        dogId = dog.getId();
    }

    @Test
    void findByDogIdAndMemberId() {
        var dogHasHandlerOptional = dogHasHandlerRepository.findByDogIdAndMemberId(dogId, memberId);

        assertThat(dogHasHandlerOptional)
                .isPresent()
                .hasValue(activeDogHasHandler);
    }

    @Nested
    class FindAllByName {

        private DogHasHandler activeDogHasHandler2;

        @BeforeEach
        void setUp() {

            var dog = TestFixture.createDog();
            dog.setName("Robert");
            dogRepository.save(dog);

            activeDogHasHandler2 = new DogHasHandler();
            dogReferenceRepository.findById(dog.getId()).ifPresent(activeDogHasHandler2::setDog);
            activeDogHasHandler2.setMember(memberRepository.save(TestFixture.createMemberReference("Maximilian", "Mustermann")));
            dogHasHandlerRepository.save(activeDogHasHandler2);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void nameIsEmptyOrNull(String name) {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByName(name, Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactlyInAnyOrder(activeDogHasHandler, activeDogHasHandler2);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Rob", "Max", "Must"})
        void findByShortDogName(String name) {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByName(name, Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler2);
        }

        @Test
        void findByDogNameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByName(activeDogHasHandler.getDog().getName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler);
        }

        @Test
        void findByMemberFirstnameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByName(activeDogHasHandler.getMember().getFirstName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler);
        }

        @Test
        void findByMemberLastnameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByName(activeDogHasHandler.getMember().getLastName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler);
        }
    }
}
