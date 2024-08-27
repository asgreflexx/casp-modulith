package casp.web.backend.data.access.layer.repositories;


import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class DogHasHandlerCustomRepositoryImplTest {

    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private MemberRepository memberRepository;

    private DogHasHandler dogHasHandler;
    private Dog dog;
    private Member member;

    @BeforeEach
    void setUp() {
        dogHasHandlerRepository.deleteAll();
        dogRepository.deleteAll();
        memberRepository.deleteAll();

        dog = new Dog();
        dog.setName("Bonsai");
        dog = dogRepository.save(dog);

        member = new Member();
        member.setFirstName("John");
        member.setLastName("Doe");
        member = memberRepository.save(member);

        dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler = dogHasHandlerRepository.save(dogHasHandler);
    }

    @Test
    void findDogHasHandlerByDogName() {
        assertThat(dogHasHandlerRepository.findAllByMemberNameOrDogName(dog.getName()))
                .containsExactly(dogHasHandler);
    }

    @Test
    void findDogHasHandlerByMemberFirstName() {
        assertThat(dogHasHandlerRepository.findAllByMemberNameOrDogName(member.getFirstName()))
                .containsExactly(dogHasHandler);
    }

    @Test
    void findDogHasHandlerByMemberLastName() {
        assertThat(dogHasHandlerRepository.findAllByMemberNameOrDogName(member.getLastName()))
                .containsExactly(dogHasHandler);
    }

    @Test
    void findDogHasHandlerByNullValue() {
        assertThat(dogHasHandlerRepository.findAllByMemberNameOrDogName(null))
                .containsExactly(dogHasHandler);
    }
}
