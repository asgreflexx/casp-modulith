package casp.web.backend.data.access.layer.commons;


import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MongoValidationConfigurationTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveInvalidInstance() {
        var member = new Member();

        assertThrows(ConstraintViolationException.class, () -> memberRepository.save(member));
        assertThat(memberRepository.findById(member.getId())).isEmpty();
    }
}
