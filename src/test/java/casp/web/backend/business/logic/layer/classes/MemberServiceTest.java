package casp.web.backend.business.logic.layer.classes;

import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.Roles;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private IMemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private UUID id;
    private UUID randomId;
    private String emailSavedMember;

    @BeforeEach
    void setUp() {
        emailSavedMember = "ida@stud.fh-campuswien.ac.at";

        randomId = UUID.randomUUID();
        id = UUID.randomUUID();
        member = new Member();
        member.setFirstName("Ida");
        member.setId(id);
        member.setEmail(emailSavedMember);
        member.getRoles().add(Roles.USER);
    }

    @Test
    void getMemberByIdHappyPath() throws Exception {
        // given
        Mockito.when(memberRepository.findById(id)).thenReturn(Optional.of(member));

        // when
        Member actualmember = memberService.getMemberById(id);

        // then
        assertEquals(member.getFirstName(), actualmember.getFirstName());
    }

    @Test
    void getMemberByIdNotFound() {
        // given
        Mockito.when(memberRepository.findById(randomId)).thenReturn(Optional.empty());

        // when + then(Exception)
        assertThrows(Exception.class, () -> memberService.getMemberById(randomId));
    }

    @Test
    void saveMemberWithSameEmail() {
        //given
        Mockito.when(memberRepository.findMemberByEmail(emailSavedMember)).thenReturn(Optional.of(member));

        Member member2 = new Member();
        member2.setEmail(emailSavedMember);
        member2.setFirstName("Michael");
        member2.setLastName("Svoboda");

        assertThrows(Exception.class, () -> memberService.saveMember(member2));
    }

    @Test
    void saveMemberWithDifferentEmail() {
        //given
        Mockito.when(memberRepository.findMemberByEmail("michael.svoboda@test.at")).thenReturn(Optional.empty());

        Member member2 = new Member();
        member2.setEmail("michael.svoboda@test.at");
        member2.setFirstName("Michael");
        member2.setLastName("Svoboda");

        assertDoesNotThrow(() -> memberService.saveMember(member2));
    }

    @Test
    void saveMemberUpdateMemberDataWithSameEmail() {
        //given
        Mockito.when(memberRepository.findMemberByEmail(emailSavedMember)).thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> memberService.saveMember(member));
    }

    @Test
    void returnMemberEmail() {
        Mockito.when(memberRepository.findAllByIdInAndEntityStatus(Set.of(member.getId()), EntityStatus.ACTIVE)).thenReturn(Set.of(member));

        Set<String> emails = memberService.getMembersEmailByIds(Set.of(member.getId()));

        assertEquals(1, emails.size());
        assertEquals(member.getEmail(), emails.iterator().next());
    }
}
