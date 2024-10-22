package casp.web.backend.configuration;

import casp.web.backend.common.enums.Role;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "casp-test-properties")
@Component
class CaspTestProperties {
    private final MemberRepository memberRepository;

    @Autowired
    CaspTestProperties(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    void setMembers(Set<MemberProperty> members) {
        members.forEach(this::createTestMemberIfDoesNotExist);
    }

    private void createTestMemberIfDoesNotExist(final MemberProperty m) {
        var doesMemberExist = memberRepository.findOneByEmail(m.email()).isPresent();
        if (!doesMemberExist) {
            final var member = new Member();
            member.setEmail(m.email());
            member.setFirstName(m.firstName());
            member.setLastName(m.lastName());
            member.setRoles(new HashSet<>(m.roles()));
            memberRepository.save(member);
        }
    }

    record MemberProperty(String email, String firstName, String lastName, Set<Role> roles) {
    }
}
