package casp.web.backend.configuration.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@ConfigurationProperties(prefix = "casp-test-properties")
@Component
class CaspTestProperties {
    private final MemberTestRepository memberRepository;

    @Autowired
    CaspTestProperties(MemberTestRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    void setMembers(Set<MemberProperty> members) {
        members.forEach(this::createTestMemberIfDoesNotExist);
    }

    private void createTestMemberIfDoesNotExist(MemberProperty member) {
        var shouldCreateMember = memberRepository.findOneByEmail(member.getEmail()).isEmpty();
        if (shouldCreateMember) {
            memberRepository.save(member);
        }
    }
}
