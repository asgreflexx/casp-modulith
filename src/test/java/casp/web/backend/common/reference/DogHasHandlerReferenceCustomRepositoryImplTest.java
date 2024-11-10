package casp.web.backend.common.reference;

import casp.web.backend.TestFixture;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.dog.data.Dog;
import casp.web.backend.dog.data.DogHasHandler;
import casp.web.backend.dog.data.DogHasHandlerRepository;
import casp.web.backend.dog.data.DogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static casp.web.backend.common.reference.DogHasHandlerReferenceMapper.DOG_HAS_HANDLER_REFERENCE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
class DogHasHandlerReferenceCustomRepositoryImplTest {

    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    private MemberReference member;
    private Dog dog;
    private DogHasHandler dogHasHandler;

    @BeforeEach
    void setUp() {
        dogHasHandlerRepository.deleteAll();
        memberReferenceRepository.deleteAll();
        dogRepository.deleteAll();

        member = new MemberReference();
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("%s@mail.com".formatted(member.getId()));
        member = memberReferenceRepository.save(member);
        dog = dogRepository.save(TestFixture.createDog());
        dogHasHandler = new DogHasHandler();
        dogHasHandler.setDog(DOG_HAS_HANDLER_REFERENCE_MAPPER.toDogReference(dog));
        dogHasHandler.setMember(member);
        dogHasHandlerRepository.save(dogHasHandler);
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
            dogRepository.save(dog);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByMemberId(member.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }

        @Test
        void dogHasHandlerIsNotActiveMemberAndDogAreActive() {
            dogHasHandler.setEntityStatus(EntityStatus.INACTIVE);
            dogHasHandlerRepository.save(dogHasHandler);

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
            dogRepository.save(dog);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }

        @Test
        void dogHasHandlerIsNotActiveMemberAndDogAreActive() {
            dogHasHandler.setEntityStatus(EntityStatus.INACTIVE);
            dogHasHandlerRepository.save(dogHasHandler);

            var actualDogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());

            assertThat(actualDogHasHandlerSet)
                    .isEmpty();
        }
    }
}
