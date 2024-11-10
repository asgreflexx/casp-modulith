package casp.web.backend.configuration;

import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import casp.web.backend.member.data.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaspTestPropertiesTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CaspTestProperties.MemberProperty memberProperty;
    @Captor
    private ArgumentCaptor<Member> memberArgumentCaptor;

    @InjectMocks
    private CaspTestProperties caspTestProperties;

    @BeforeEach
    void setUp() {
        when(memberProperty.email()).thenReturn("test@example.com");
    }

    @Nested
    class SetMembers {
        @Test
        void testMemberDoesNotExist() {
            when(memberProperty.firstName()).thenReturn("John");
            when(memberProperty.lastName()).thenReturn("Doe");
            when(memberProperty.roles()).thenReturn(Set.of(Role.ADMIN));
            when(memberRepository.findOneByEmail(memberProperty.email())).thenReturn(Optional.empty());

            caspTestProperties.setMembers(Set.of(memberProperty));

            verify(memberRepository).save(memberArgumentCaptor.capture());

            var domainMember = memberArgumentCaptor.getValue();
            assertEquals(memberProperty.email(), domainMember.getEmail());
            assertEquals(memberProperty.firstName(), domainMember.getFirstName());
            assertEquals(memberProperty.lastName(), domainMember.getLastName());
            assertThat(memberProperty.roles()).containsExactlyInAnyOrderElementsOf(domainMember.getRoles());
        }

        @Test
        void testMemberExist() {
            when(memberRepository.findOneByEmail(memberProperty.email())).thenReturn(Optional.of(mock(Member.class)));

            caspTestProperties.setMembers(Set.of(memberProperty));

            verify(memberRepository, times(0)).save(any(Member.class));
        }
    }
}
