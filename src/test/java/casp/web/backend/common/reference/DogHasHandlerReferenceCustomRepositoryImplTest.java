package casp.web.backend.common.reference;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.common.enums.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
class DogHasHandlerReferenceCustomRepositoryImplTest {

    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    @Autowired
    private DogReferenceRepository dogReferenceRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    private MemberReference member;
    private DogReference dog;
    private DogHasHandlerReference dogHasHandler;

    @BeforeEach
    void setUp() {
        dogHasHandlerReferenceRepository.deleteAll();
        memberReferenceRepository.deleteAll();
        dogReferenceRepository.deleteAll();

        member = memberReferenceRepository.save(ReferenceTestFixture.createMemberReference());
        dog = dogReferenceRepository.save(ReferenceTestFixture.createDogReference());
        dogHasHandler = new DogHasHandlerReference();
        dogHasHandler.setDog(dog);
        dogHasHandler.setMember(member);
        dogHasHandlerReferenceRepository.save(dogHasHandler);
    }

    @Nested
    class FindAllByMemberId {
        @Test
        void memberDogAndDogHasHandlerAreActive() {
            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByMemberId(member.getId());

            assertThat(actualDogHasHandlerSet)
                    .singleElement()
                    .satisfies(a -> assertEquals(dogHasHandler.getId(), a.getId()));
        }

        @Test
        void memberIsNotActiveDogAndDogHasHandlerAreActive() {
            member.setEntityStatus(EntityStatus.INACTIVE);
            memberReferenceRepository.save(member);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByMemberId(member.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }

        @Test
        void dogIsNotActiveMemberAndDogHasHandlerAreActive() {
            dog.setEntityStatus(EntityStatus.INACTIVE);
            dogReferenceRepository.save(dog);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByMemberId(member.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }

        @Test
        void dogHasHandlerIsNotActiveMemberAndDogAreActive() {
            dogHasHandler.setEntityStatus(EntityStatus.INACTIVE);
            dogHasHandlerReferenceRepository.save(dogHasHandler);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByMemberId(member.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }
    }

    @Nested
    class FindAllByDogId {
        @Test
        void memberDogAndDogHasHandlerAreActive() {
            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());

            assertThat(actualDogHasHandlerSet)
                    .singleElement()
                    .satisfies(a -> assertEquals(dogHasHandler.getId(), a.getId()));
        }

        @Test
        void memberIsNotActiveDogAndDogHasHandlerAreActive() {
            member.setEntityStatus(EntityStatus.INACTIVE);
            memberReferenceRepository.save(member);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }

        @Test
        void dogIsNotActiveMemberAndDogHasHandlerAreActive() {
            dog.setEntityStatus(EntityStatus.INACTIVE);
            dogReferenceRepository.save(dog);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }

        @Test
        void dogHasHandlerIsNotActiveMemberAndDogAreActive() {
            dogHasHandler.setEntityStatus(EntityStatus.INACTIVE);
            dogHasHandlerReferenceRepository.save(dogHasHandler);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }
    }
}
