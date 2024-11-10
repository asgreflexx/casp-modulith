package casp.web.backend.dog.data;


import casp.web.backend.TestFixture;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.member.data.MemberRepository;
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
    private MemberRepository memberRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;

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
        createMemberAndReturnItsId();

        activeDogHasHandler = createDogHasHandler(EntityStatus.ACTIVE);
        inactiveDogHasHandler = createDogHasHandler(EntityStatus.INACTIVE);
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

    private DogHasHandler createDogHasHandler(EntityStatus entityStatus) {
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setEntityStatus(entityStatus);
        dogReferenceRepository.findById(dogId).ifPresent(dogHasHandler::setDog);
        memberReferenceRepository.findById(memberId).ifPresent(dogHasHandler::setMember);
        return dogHasHandlerRepository.save(dogHasHandler);
    }

    private void createMemberAndReturnItsId() {
        var member = TestFixture.createMember();
        member.setFirstName("John");
        member.setLastName("Doe");
        memberRepository.save(member);
        memberId = member.getId();
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
            var member = TestFixture.createMember();
            member.setFirstName("Maximilian");
            member.setLastName("Mustermann");
            memberRepository.save(member);

            var dog = TestFixture.createDog();
            dog.setName("Robert");
            dogRepository.save(dog);

            activeDogHasHandler2 = new DogHasHandler();
            dogReferenceRepository.findById(dog.getId()).ifPresent(activeDogHasHandler2::setDog);
            memberReferenceRepository.findById(member.getId()).ifPresent(activeDogHasHandler2::setMember);
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
