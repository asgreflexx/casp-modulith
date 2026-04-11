package casp.web.backend.member.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReferenceRepository;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataMongoTest
class MemberCustomRepositoryImplTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

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

    @Test
    void findAllActiveMembersEmails() {
        assertThat(memberRepository.findAllActiveMembersEmails())
                .containsExactlyInAnyOrder(john.getEmail(), doe.getEmail());
    }

    @Test
    void getMembershipFeesStats() {
        addMembershipFee(LocalDate.now(), john);
        addMembershipFee(LocalDate.now(), john);
        addMembershipFee(LocalDate.now().minusYears(1), john);
        addMembershipFee(LocalDate.now().minusYears(3), john);
        doe.setEntityStatus(EntityStatus.INACTIVE);
        addMembershipFee(LocalDate.now(), doe);

        var membershipFeesStats = memberRepository.getMembershipFeesStats();

        var thisYear = membershipFeesStats.thisYear();
        var lastYear = membershipFeesStats.lastYear();
        var twoYearsAgo = membershipFeesStats.twoYearsAgo();
        var year = LocalDate.now().getYear();
        assertEquals(year, thisYear.year());
        assertEquals(2.0, thisYear.totalPaid());
        assertEquals(year - 1, lastYear.year());
        assertEquals(1.0, lastYear.totalPaid());
        assertEquals(year - 2, twoYearsAgo.year());
        assertEquals(0.0, twoYearsAgo.totalPaid());
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

    private void addMembershipFee(LocalDate paidDate, Member member) {
        var membershipFee = new MembershipFee();
        membershipFee.setPaidDate(paidDate);
        membershipFee.setPaidPrice(1.0);
        member.getMembershipFees().add(membershipFee);
        memberRepository.save(member);
    }
}

