package casp.web.backend.member.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataMongoTest
class MemberCustomRepositoryImplTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DogReferenceRepository dogRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerRepository;
    private Member john;
    private Member doe;

    @BeforeEach
    void setUp() {
        dogRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        memberRepository.deleteAll();

        john = createMember("John", "John", EntityStatus.ACTIVE, Role.ADMIN);
        doe = createMember("Doe", "Doe", EntityStatus.ACTIVE, Role.CASHIER);
        createMember("deleted", "deleted", EntityStatus.DELETED, null);
    }

    private Member createMember(String firstName, String lastName, EntityStatus entityStatus, @Nullable Role role) {
        var member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEntityStatus(entityStatus);
        member.setEmail("%s.%s@mail.com".formatted(firstName.toLowerCase(), lastName.toLowerCase()));
        Optional.ofNullable(role).ifPresent(member.getRoles()::add);
        member = memberRepository.save(member);
        return member;
    }

    @Test
    void findAllActiveMembersEmails() {
        assertThat(memberRepository.findAllActiveMembersEmails())
                .containsExactlyInAnyOrder(john.getEmail(), doe.getEmail());
    }

    @Nested
    class FindAllByValue {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"    "})
        void findAllWithoutValue(String name) {
            assertThat(memberRepository.findAllByEntityStatusNameAndRoles(EntityStatus.ACTIVE, name, null, Pageable.unpaged())).containsExactlyInAnyOrder(doe, john);
        }

        @Test
        void findOneByName() {
            assertThat(memberRepository.findAllByEntityStatusNameAndRoles(EntityStatus.ACTIVE, "John", null, Pageable.unpaged())).containsExactly(john);
        }

        @Test
        void findAllByMultipleLettersSeparatedBySpaces() {
            assertThat(memberRepository.findAllByEntityStatusNameAndRoles(EntityStatus.ACTIVE, "J X D", null, Pageable.unpaged())).containsExactlyInAnyOrder(doe, john);
        }

        @Test
        void findAllByRoles() {
            assertThat(memberRepository.findAllByEntityStatusNameAndRoles(EntityStatus.ACTIVE, null, Set.of(Role.CASHIER), Pageable.unpaged())).containsExactlyInAnyOrder(doe);
        }
    }

    @Nested
    class FindByIdAndEntityStatus {
        @Test
        void memberExist() {
            assertEquals(john, memberRepository.findByIdAndEntityStatusCustom(john.getId(), EntityStatus.ACTIVE));
        }

        @Test
        void memberNotFound() {
            var memberId = UUID.randomUUID();
            assertThrows(NoSuchElementException.class, () -> memberRepository.findByIdAndEntityStatusCustom(memberId, EntityStatus.DELETED));
        }
    }

    @Nested
    class FindAllByNotDogId {
        private DogReference dogReference;

        @BeforeEach
        void setUp() {
            var johnReference = new MemberReference();
            johnReference.setId(john.getId());
            johnReference.setFirstName(john.getFirstName());
            johnReference.setLastName(john.getLastName());
            johnReference.setEmail(john.getEmail());
            dogReference = new DogReference();
            dogReference.setName("Bella");
            dogReference = dogRepository.save(dogReference);
            var dogHasHandler = new DogHasHandlerReference();
            dogHasHandler.setDog(dogReference);
            dogHasHandler.setMember(johnReference);
            dogHasHandlerRepository.save(dogHasHandler);
        }

        @Test
        void byDogId() {
            assertThat(memberRepository.findAllByNotDogId(dogReference.getId(), null, Pageable.unpaged()))
                    .containsExactly(doe);
        }

        @Test
        void byDogIdAndFirstName() {
            assertThat(memberRepository.findAllByNotDogId(dogReference.getId(), john.getFirstName(), Pageable.unpaged()))
                    .isEmpty();
        }

        @Test
        void byDogIdAndLastName() {
            assertThat(memberRepository.findAllByNotDogId(dogReference.getId(), john.getLastName(), Pageable.unpaged()))
                    .isEmpty();
        }
    }
}

