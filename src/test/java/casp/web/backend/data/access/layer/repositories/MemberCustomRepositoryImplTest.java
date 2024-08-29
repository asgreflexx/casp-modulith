package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.Role;
import casp.web.backend.data.access.layer.documents.member.Member;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataMongoTest
class MemberCustomRepositoryImplTest {

    @Autowired
    private MemberRepository memberRepository;
    private Member john;
    private Member doe;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        john = createMember("John", "John", EntityStatus.ACTIVE, Role.ADMIN);
        doe = createMember("Doe", "Doe", EntityStatus.ACTIVE, Role.CASHIER);
        createMember("deleted", "deleted", EntityStatus.DELETED, null);
    }

    private Member createMember(final String firstName, final String lastName, final EntityStatus entityStatus, @Nullable final Role role) {
        var member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEntityStatus(entityStatus);
        Optional.ofNullable(role).ifPresent(member.getRoles()::add);
        member = memberRepository.save(member);
        return member;
    }

    @Nested
    class FindAllByFirstNameAndLastName {

        @Test
        void findNoneByFirstNameAndLastName() {
            assertThat(memberRepository.findAllByFirstNameAndLastName("John", "Doe")).isEmpty();
        }

        @Test
        void findOneByFirstName() {
            assertThat(memberRepository.findAllByFirstNameAndLastName("John", null)).containsExactly(john);
        }

        @Test
        void findOneByLastName() {
            assertThat(memberRepository.findAllByFirstNameAndLastName(null, "John")).containsExactly(john);
        }

        @Test
        void findAllWithoutValues() {
            assertThat(memberRepository.findAllByFirstNameAndLastName(null, null)).containsExactly(doe, john);
        }
    }

    @Nested
    class FindAllByValue {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"    "})
        void findAllWithoutValue(String name) {
            assertThat(memberRepository.findAllByValue(name, Pageable.unpaged())).containsExactlyInAnyOrder(doe, john);
        }

        @Test
        void findOneByName() {
            assertThat(memberRepository.findAllByValue("John", Pageable.unpaged())).containsExactly(john);
        }

        @Test
        void findAllByMultipleLettersSeparatedBySpaces() {
            assertThat(memberRepository.findAllByValue("J X D", Pageable.unpaged())).containsExactlyInAnyOrder(doe, john);
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
}

