package casp.web.backend.member;

import casp.web.backend.common.exception.MemberEMailConflictException;
import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static casp.web.backend.member.MemberMapper.MEMBER_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@ExtendWith(OutputCaptureExtension.class)
@Testcontainers
@SpringBootTest
class MemberServiceImplIntTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @Nested
    class EmailUniqueness {

        private Member member;

        @BeforeEach
        void setUp() {
            memberRepository.deleteAll();
            member = memberRepository.save(TestFixture.createMember());
        }

        @Test
        void doesNotFail() {
            memberService.deleteMemberById(member.getId());

            assertDoesNotThrow(() -> memberService.saveMember(MEMBER_MAPPER.toTarget(TestFixture.createMember())));
        }

        @Test
        void doesFail(CapturedOutput output) {
            var exception = assertThrowsExactly(MemberEMailConflictException.class, () -> memberService.saveMember(MEMBER_MAPPER.toTarget(TestFixture.createMember())));

            assertThat(output).contains(LogLevel.ERROR.name(), exception.getMessage());
        }
    }
}
