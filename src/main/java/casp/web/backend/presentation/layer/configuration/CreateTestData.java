package casp.web.backend.presentation.layer.configuration;

import casp.web.backend.business.logic.layer.interfaces.IMemberService;
import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.entities.DogHasHandler;
import casp.web.backend.data.access.layer.entities.Grade;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.Roles;
import casp.web.backend.data.access.layer.repositories.IDogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.IDogRepository;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "spring.jpa.hibernate.ddl-auto", havingValue = "create")
public class CreateTestData implements ApplicationRunner {

    private final IMemberRepository memberRepository;
    private final IDogRepository dogRepository;
    private final IDogHasHandlerRepository dogHasHandlerRepository;

    @Autowired
    CreateTestData(IMemberRepository memberRepository, IMemberService memberService, IDogRepository dogRepository, IDogHasHandlerRepository dogHasHandlerRepository) {
        this.memberRepository = memberRepository;
        this.dogRepository = dogRepository;
        this.dogHasHandlerRepository = dogHasHandlerRepository;
    }

    public void clearDatabase() {
        dogHasHandlerRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();
    }

    private List<Member> createMembers(Roles role) {

        Member member;

        member = new Member();
        member.setFirstName("Ciao");
        member.setLastName("Bella");
        member.setEmail("ciao@gmail.com");
        member.getRoles().add(role);
        memberRepository.save(member);

        member = new Member();
        member.setFirstName("Tante");
        member.setLastName("Anni");
        member.setEmail("anni@gmail.com");
        member.getRoles().add(role);
        memberRepository.save(member);

        member = new Member();
        member.setFirstName("Onkel");
        member.setLastName("lluis");
        member.setEmail("Lluis@gmail.com");
        member.getRoles().add(role);
        memberRepository.save(member);

        member = new Member();
        member.setFirstName("Tante");
        member.setLastName("Sarah");
        member.setEmail("sarah@gmail.com");
        member.getRoles().add(role);
        memberRepository.save(member);

        member = new Member();
        member.setFirstName("Onkel");
        member.setLastName("Jimmy");
        member.setEmail("jimmy@gmail.com");
        member.getRoles().add(role);
        memberRepository.save(member);

        for (int i = 0; i < 10; i++) {
            member = new Member();
            member.setFirstName("Ciao");
            member.setLastName("Bella" + i);
            member.setEmail("ciao" + i + "@gmail.com");
            member.getRoles().add(role);
            memberRepository.save(member);
        }

        return memberRepository.findAll();
    }

    private List<Dog> createDogs() {
        Dog dog;

        for (int i = 0; i < 10; i++) {
            dog = new Dog();
            dog.setName("Doggo" + i);
            dog.setOwnerName("Harry Potter");
            dog.setOwnerAddress("Privat Drive Nr9");
            dogRepository.save(dog);
        }


        return dogRepository.findAll();
    }

    private List<DogHasHandler> createDogHasHandler(List<Member> memberList, List<Dog> dogList) {

        DogHasHandler dogHasHandler;

        for (int i = 0; i < 10; i++) {
            dogHasHandler = new DogHasHandler();
            dogHasHandler.setMemberId(memberList.get(i).getId());
            dogHasHandler.setDogId(dogList.get(i).getId());
            createGrades(dogHasHandler);
            dogHasHandlerRepository.save(dogHasHandler);
        }
        return dogHasHandlerRepository.findAll();

    }

    private void createGrades(DogHasHandler dogHasHandler) {
        Grade grade;

        for (int i = 0; i < 10; i++) {
            grade = new Grade();
            grade.setName("Grade" + i);
            dogHasHandler.getGrades().add(grade);
        }


    }


    @Override
    public void run(ApplicationArguments args) {
        clearDatabase();
        List<Member> memberList = createMembers(Roles.ADMIN);
        List<Dog> dogList = createDogs();
        createDogHasHandler(memberList, dogList);
    }
}
