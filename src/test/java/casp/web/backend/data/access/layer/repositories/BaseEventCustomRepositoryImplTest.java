package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.TestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class BaseEventCustomRepositoryImplTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BaseEventRepository baseEventRepository;

    @Test
    void findAllByYear() {
        baseEventRepository.deleteAll();
        memberRepository.deleteAll();
        var member = memberRepository.save(TestFixture.createValidMember());
        var event = TestFixture.createValidEvent();
        event.setMember(member);
        event.setMemberId(member.getId());
        event.setMinLocalDateTime(LocalDateTime.now().minusDays(2));
        event.setMaxLocalDateTime(LocalDateTime.now().minusDays(1));
        event = baseEventRepository.save(event);

        assertThat(baseEventRepository.findAllByYearAndEventType(event.getMinLocalDateTime().getYear(), event.getEventType(), Pageable.unpaged())).containsExactly(event);
    }
}
