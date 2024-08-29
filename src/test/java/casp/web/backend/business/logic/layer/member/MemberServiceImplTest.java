package casp.web.backend.business.logic.layer.member;

import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.Role;
import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DogHasHandlerService dogHasHandlerService;

    @InjectMocks
    private MemberServiceImpl memberService;
    private Member member;

    @BeforeEach
    void setUp() {
        member = spy(new Member());
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("member@casp.com");
    }

    @Test
    void getMembersByFirstNameOrLastName() {
        when(memberRepository.findAllByFirstNameAndLastName(member.getFirstName(), member.getLastName())).thenReturn(List.of(member));

        assertThat(memberService.getMembersByFirstNameAndLastName(member.getFirstName(), member.getLastName())).containsExactly(member);
    }

    @Test
    void getAllAvailableRoles() {
        assertThat(memberService.getAllAvailableRoles()).containsExactlyElementsOf(Role.getAllRolesSorted());
    }

    @Test
    void getMembersByEntityStatus() {
        var page = new PageImpl<>(List.of(member));
        when(memberRepository.findAllByEntityStatus(EntityStatus.ACTIVE, Pageable.unpaged())).thenReturn(page);

        assertThat(memberService.getMembersByEntityStatus(EntityStatus.ACTIVE, Pageable.unpaged())).containsExactly(member);
    }

    @Test
    void getMembersByName() {
        var page = new PageImpl<>(List.of(member));
        when(memberRepository.findAllByValue(member.getLastName(), Pageable.unpaged())).thenReturn(page);

        assertThat(memberService.getMembersByName(member.getLastName(), Pageable.unpaged())).containsExactly(member);
    }

    @Test
    void getMembersEmailByIds() {
        when(memberRepository.findAllByIdInAndEntityStatus(Collections.singleton(member.getId()), EntityStatus.ACTIVE)).thenReturn(Set.of(member));

        assertThat(memberService.getMembersEmailByIds(Set.of(member.getId()))).containsExactly(member.getEmail());
    }

    @Nested
    class GetMemberById {

        @Test
        void memberExist() {
            when(memberRepository.findByIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            assertSame(member, memberService.getMemberById(member.getId()));
        }

        @Test
        void memberDoesNotExist() {
            var memberId = UUID.randomUUID();
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> memberService.getMemberById(memberId));
        }

    }

    @Nested
    class SaveMember {
        @Test
        void emailDoesNotExists() {
            when(memberRepository.save(member)).thenReturn(member);

            assertSame(member, memberService.saveMember(member));
        }

        @Test
        void emailExists() {
            when(memberRepository.findMemberByEmail(member.getEmail())).thenReturn(Optional.of(new Member()));

            assertThrows(IllegalStateException.class, () -> memberService.saveMember(member));
        }
    }

    @Nested
    class DeleteMemberById {
        @Test
        void memberDoesNotExist() {
            var memberId = UUID.randomUUID();
            when(memberRepository.findByIdAndEntityStatusNot(memberId, EntityStatus.DELETED)).thenReturn(Optional.empty());

            memberService.deleteMemberById(memberId);

            verifyNoInteractions(dogHasHandlerService);

        }

        @Test
        void memberExist() {
            when(memberRepository.findByIdAndEntityStatusNot(member.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(member));

            memberService.deleteMemberById(member.getId());

            verify(dogHasHandlerService).deleteDogHasHandlersByMemberId(member.getId());
            verify(member).setEntityStatus(EntityStatus.DELETED);
            verify(member).setEmail("%s---%s".formatted(member.getEmail(), member.getId()));
        }
    }

    @Nested
    class DeactivateMember {
        @Test
        void memberDoesNotExist() {
            var memberId = UUID.randomUUID();
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> memberService.deactivateMember(memberId));
        }

        @Test
        void memberExist() {
            when(memberRepository.findByIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            assertSame(EntityStatus.INACTIVE, memberService.deactivateMember(member.getId()).getEntityStatus());

            verify(dogHasHandlerService).deactivateDogHasHandlersByMemberId(member.getId());
        }
    }

    @Nested
    class ActivateMember {
        @Test
        void memberDoesNotExist() {
            var memberId = UUID.randomUUID();
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.INACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> memberService.activateMember(memberId));
        }

        @Test
        void memberExist() {
            when(memberRepository.findByIdAndEntityStatus(member.getId(), EntityStatus.INACTIVE)).thenReturn(Optional.of(member));

            assertSame(EntityStatus.ACTIVE, memberService.activateMember(member.getId()).getEntityStatus());

            verify(dogHasHandlerService).activateDogHasHandlersByMemberId(member.getId());
        }
    }
}
