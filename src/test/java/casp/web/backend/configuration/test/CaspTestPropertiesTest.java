package casp.web.backend.configuration.test;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaspTestPropertiesTest {
    @Mock
    private MemberTestRepository memberRepository;
    @Mock
    private MemberProperty memberProperty;
    @Captor
    private ArgumentCaptor<MemberProperty> memberArgumentCaptor;

    @InjectMocks
    private CaspTestProperties caspTestProperties;

    @BeforeEach
    void setUp() {
        when(memberProperty.getEmail()).thenReturn("test@example.com");
    }

    @Nested
    class SetMembers {
        @Test
        void testMemberDoesNotExist() {
            when(memberProperty.getFirstName()).thenReturn("John");
            when(memberProperty.getLastName()).thenReturn("Doe");
            when(memberProperty.getRoles()).thenReturn(Set.of(RoleTest.ADMIN));
            when(memberRepository.findOneByEmail(memberProperty.getEmail())).thenReturn(Optional.empty());

            caspTestProperties.setMembers(Set.of(memberProperty));

            verify(memberRepository).save(memberArgumentCaptor.capture());

            var domainMember = memberArgumentCaptor.getValue();
            assertEquals(memberProperty.getEmail(), domainMember.getEmail());
            assertEquals(memberProperty.getFirstName(), domainMember.getFirstName());
            assertEquals(memberProperty.getLastName(), domainMember.getLastName());
            assertThat(memberProperty.getRoles()).containsExactlyInAnyOrderElementsOf(domainMember.getRoles());
        }

        @Test
        void testMemberExist() {
            when(memberRepository.findOneByEmail(memberProperty.getEmail())).thenReturn(Optional.of(memberProperty));

            caspTestProperties.setMembers(Set.of(memberProperty));

            verify(memberRepository, times(0)).save(memberProperty);
        }
    }
}
