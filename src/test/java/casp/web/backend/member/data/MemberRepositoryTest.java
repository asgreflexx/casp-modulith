package casp.web.backend.member.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.member.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataMongoTest
class MemberRepositoryTest {
    private static final String CREATED_BY = "test";
    @Autowired
    private MemberRepository memberRepository;
    private Member member;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();

        member = TestFixture.createMember();
        member.setCreatedBy(CREATED_BY);
        member = memberRepository.save(member);
    }

    @Nested
    class SetMetadataAndSave {
        @Test
        void createNewMember() {
            var newMember = TestFixture.createMember();
            assertEquals(newMember, memberRepository.setMetadataAndSave(newMember));
        }

        @Test
        void setCreatedByAndDate() {
            member.setCreatedBy(null);
            member.setCreated(null);

            var actualMember = memberRepository.setMetadataAndSave(member);

            assertEquals(CREATED_BY, actualMember.getCreatedBy());
            assertThat(actualMember.getCreated())
                    .isCloseTo(member.getCreated(), within(1, ChronoUnit.SECONDS));
        }

        @ParameterizedTest
        @EnumSource(value = EntityStatus.class, names = {"INACTIVE", "DELETED"})
        void invalidStates(EntityStatus entityStatus) {
            member.setEntityStatus(entityStatus);
            member = memberRepository.save(member);
            member.setEntityStatus(EntityStatus.ACTIVE);

            assertThrows(IllegalStateException.class, () -> memberRepository.setMetadataAndSave(member));
        }
    }
}
