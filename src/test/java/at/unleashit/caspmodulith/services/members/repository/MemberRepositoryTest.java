package at.unleashit.caspmodulith.services.members.repository;

import at.unleashit.caspmodulith.enums.EntityStatus;
import at.unleashit.caspmodulith.services.members.enums.Gender;
import at.unleashit.caspmodulith.services.members.enums.Roles;
import at.unleashit.caspmodulith.services.members.model.Member;
import at.unleashit.caspmodulith.services.members.model.MembershipFee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = MongoDbTestContainerConfig.class)
public class MemberRepositoryTest {
    
    @Autowired
    MemberRepository memberRepository;

    private UUID uuid;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        uuid = UUID.randomUUID();
        memberRepository.save(
                new Member(
                    uuid,
                    0,
                    "Michael",
                    LocalDateTime.now(),
                    "Michael",
                    LocalDateTime.now(),
                    EntityStatus.ACTIVE,
                    "Michael",
                    "Svoboda",
                    LocalDate.now(),
                    Gender.MALE,
                    "02002301932019",
                    "michael.svoboda@unleash-it.at",
                    "Teststreet 1",
                    "1210",
                    "Vienna",
                    Set.of(Roles.ADMIN, Roles.CASHIER),
                    Set.of(
                            new MembershipFee(
                                Float.valueOf(12),
                                true,
                                "Testcomment",
                                LocalDate.now()
                            )
                    )
                )
        );
    }

    @Test
    void testFindAllById() {
        Assertions.assertNotNull(memberRepository.findAllById(uuid.toString()));
    }

    @Test
    void testFindById() {
        Assertions.assertEquals("Michael", memberRepository.findById(uuid).get().firstName());
    }

    @Test
    void testFindAllByFirstNameAndLastName() {
        Assertions.assertEquals(1, memberRepository.findAllByFirstNameOrLastName("Michael", null).size());
    }

    @Test
    void testFindAllByFirstNameAndLastName2() {
        Assertions.assertEquals(1, memberRepository.findAllByFirstNameOrLastName(null, "Svoboda").size());
    }

    @Test
   void testFindAllMembersByRoles() {
        Assertions.assertEquals(1, memberRepository.findAllMembersByRoles(Roles.ADMIN, EntityStatus.ACTIVE).size());
   }

   @Test
   void testCountAllByEntityStatus() {
        Assertions.assertEquals(1, memberRepository.countAllByEntityStatus(EntityStatus.ACTIVE));
   }

}
