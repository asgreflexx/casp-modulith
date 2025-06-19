package casp.web.backend.member;

import casp.web.backend.calendar.BaseEventObserver;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.deprecated.member.Card;
import casp.web.backend.deprecated.member.CardRepository;
import casp.web.backend.deprecated.member.MemberOldRepository;
import casp.web.backend.deprecated.member.MembershipFee;
import casp.web.backend.dog.DogHasHandlerService;
import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.member.MemberMapper.MEMBER_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private MemberOldRepository memberOldRepository;

    @Mock
    private DogHasHandlerService dogHasHandlerService;
    @Mock
    private BaseEventObserver baseEventObserver;

    @Captor
    private ArgumentCaptor<Member> memberCaptor;

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
    void getMembersByEntityStatusNameAndRoles() {
        var page = new PageImpl<>(List.of(member));
        var name = "name";
        when(memberRepository.findAllByEntityStatusNameAndRoles(EntityStatus.ACTIVE, name, null, Pageable.unpaged())).thenReturn(page);

        var memberDtoPage = memberService.getMembersByEntityStatusNameAndRoles(EntityStatus.ACTIVE, name, null, Pageable.unpaged());

        assertThat(memberDtoPage).containsExactly(MEMBER_MAPPER.toTarget(member));
    }

    @Test
    void getMembersEmailByIds() {
        when(memberRepository.findAllByIdInAndEntityStatus(Collections.singleton(member.getId()), EntityStatus.ACTIVE)).thenReturn(Set.of(member));

        assertThat(memberService.getMembersEmailByIds(Set.of(member.getId()))).containsExactly(member.getEmail());
    }

    @Test
    void migrateDataToV2() {
        var membershipFee = mock(MembershipFee.class);
        when(membershipFee.getPaidPrice()).thenReturn(1.0);
        when(membershipFee.getPaidDate()).thenReturn(LocalDate.now());
        when(membershipFee.getComment()).thenReturn("comment");

        var card = mock(Card.class);
        when(card.getCode()).thenReturn(UUID.randomUUID().toString());

        var memberOld = mock(casp.web.backend.deprecated.member.Member.class);
        when(memberOld.getId()).thenReturn(UUID.randomUUID());
        when(memberOld.getMembershipFees()).thenReturn(Set.of(membershipFee));

        when(memberOldRepository.findAll()).thenReturn(List.of(memberOld));
        when(cardRepository.findAllByMemberId(memberOld.getId())).thenReturn(Set.of(card));

        memberService.migrateDataToV2();

        verify(memberRepository).save(memberCaptor.capture());
        var memberV2 = memberCaptor.getValue();
        assertEquals(memberOld.getId(), memberV2.getId());
        assertThat(memberV2.getMembershipFees())
                .singleElement()
                .satisfies(mfv2 -> {
                    assertEquals(membershipFee.getPaidDate(), mfv2.getPaidDate());
                    assertEquals(membershipFee.getPaidPrice(), mfv2.getPaidPrice());
                    assertEquals(membershipFee.getComment(), mfv2.getComment());
                });
        assertThat(memberV2.getCards())
                .singleElement()
                .satisfies(cardV2 -> assertEquals(card.getCode(), cardV2.getCode()));
    }

    @Test
    void getActiveMembersEmail() {
        var email = "mail@mail.com";
        when(memberRepository.findAllActiveMembersEmails()).thenReturn(Set.of(email));

        var emailSet = memberService.getActiveMembersEmail();

        assertThat(emailSet).containsExactly(email);
    }

    @Test
    void getMembersByNotDogId() {
        var dogId = UUID.randomUUID();
        when(memberRepository.findAllByNotDogId(dogId, null, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(member)));

        assertThat(memberService.getMembersByNotDogId(dogId, null, Pageable.unpaged()))
                .containsExactly(MEMBER_MAPPER.toTarget(member));
    }


    @Nested
    class GetMemberId {
        @Test
        void notDeleted() {
            when(memberRepository.findOneByIdAndEntityStatusNot(member.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(member));

            var memberDto = memberService.getMemberById(member.getId());

            assertThat(memberDto)
                    .usingRecursiveAssertion()
                    .isEqualTo(MEMBER_MAPPER.toTarget(member));
        }

        @Test
        void deleted() {
            var memberId = member.getId();
            when(memberRepository.findOneByIdAndEntityStatusNot(memberId, EntityStatus.DELETED)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> memberService.getMemberById(memberId));
        }
    }

    @Nested
    class SaveMember {
        @Test
        void emailDoesNotExists() {
            when(memberRepository.save(argThat(m -> member.getId() == m.getId()))).thenAnswer(i -> i.getArgument(0));

            memberService.saveMember(MEMBER_MAPPER.toTarget(member));

            verify(memberRepository).save(memberCaptor.capture());
            assertThat(memberCaptor.getValue())
                    .usingRecursiveComparison()
                    .isEqualTo(member);
        }

        @Test
        void emailExistsButBelongsToOtherMember() {
            when(memberRepository.findOneByEmail(member.getEmail())).thenReturn(Optional.of(new Member()));
            var memberDto = MEMBER_MAPPER.toTarget(member);

            assertThrows(IllegalStateException.class, () -> memberService.saveMember(memberDto));
        }

        @Test
        void updateMember() {
            when(memberRepository.findOneByEmail(member.getEmail())).thenReturn(Optional.of(member));
            when(memberRepository.save(argThat(m -> member.getId() == m.getId()))).thenAnswer(i -> i.getArgument(0));

            memberService.saveMember(MEMBER_MAPPER.toTarget(member));

            verify(memberRepository).save(memberCaptor.capture());
            assertThat(memberCaptor.getValue())
                    .usingRecursiveComparison()
                    .isEqualTo(member);
        }

        @Test
        void memberIsDisabled() {
            var memberDto = MEMBER_MAPPER.toTarget(member);

            when(memberRepository.findByIdAndEntityStatusCustom(member.getId(), EntityStatus.ACTIVE)).thenThrow(new NoSuchElementException());

            assertThrows(NoSuchElementException.class, () -> memberService.saveMember(memberDto));
        }
    }

    @Nested
    class DeleteMemberById {
        @Test
        void memberDoesNotExist() {
            var memberId = UUID.randomUUID();
            when(memberRepository.findByIdAndEntityStatusCustom(memberId, EntityStatus.ACTIVE)).thenThrow(new NoSuchElementException());

            assertThrows(NoSuchElementException.class, () -> memberService.deleteMemberById(memberId));

            verifyNoInteractions(dogHasHandlerService, baseEventObserver);

        }

        @Test
        void memberExist() {
            when(memberRepository.findByIdAndEntityStatusCustom(member.getId(), EntityStatus.ACTIVE)).thenReturn(member);

            memberService.deleteMemberById(member.getId());

            verify(dogHasHandlerService).deleteDogHasHandlersByMemberId(member.getId());
            verify(baseEventObserver).deleteBaseEventsByMemberId(member.getId());
            verify(member).setEntityStatus(EntityStatus.DELETED);
            verify(member).setEmail("%s---%s".formatted(member.getEmail(), member.getId()));
        }
    }

    @Nested
    class ToggleStatus {
        @Test
        void deleted() {
            var memberId = member.getId();
            when(memberRepository.findOneByIdAndEntityStatusNot(memberId, EntityStatus.DELETED)).thenThrow(new NoSuchElementException());

            assertThrows(NoSuchElementException.class, () -> memberService.toggleStatus(memberId));
        }

        @Test
        void deactivate() {
            when(memberRepository.findOneByIdAndEntityStatusNot(member.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(member));
            when(memberRepository.save(member)).thenAnswer(i -> i.getArgument(0));

            var memberDto = memberService.toggleStatus(member.getId());

            assertSame(EntityStatus.INACTIVE, memberDto.getEntityStatus());
            verify(dogHasHandlerService).deactivateDogHasHandlersByMemberId(member.getId());
            verify(baseEventObserver).deactivateBaseEventsByMemberId(member.getId());
        }

        @Test
        void activate() {
            member.setEntityStatus(EntityStatus.INACTIVE);
            when(memberRepository.findOneByIdAndEntityStatusNot(member.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(member));
            when(memberRepository.save(member)).thenAnswer(i -> i.getArgument(0));

            var memberDto = memberService.toggleStatus(member.getId());

            assertSame(EntityStatus.ACTIVE, memberDto.getEntityStatus());
            verify(dogHasHandlerService).activateDogHasHandlersByMemberId(member.getId());
            verify(baseEventObserver).activateBaseEventsByMemberId(member.getId());
        }
    }
}
