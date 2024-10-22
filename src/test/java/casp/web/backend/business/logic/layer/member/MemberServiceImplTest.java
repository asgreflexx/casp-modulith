package casp.web.backend.business.logic.layer.member;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import casp.web.backend.business.logic.layer.event.participants.BaseParticipantObserver;
import casp.web.backend.business.logic.layer.event.types.BaseEventObserver;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.types.Event;
import casp.web.backend.deprecated.member.Card;
import casp.web.backend.deprecated.member.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

import static casp.web.backend.business.logic.layer.member.MemberMapper.MEMBER_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    private DogHasHandlerService dogHasHandlerService;
    @Mock
    private BaseParticipantObserver baseParticipantObserver;
    @Mock
    private BaseEventObserver baseEventObserver;
    @Mock
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

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
    void getMembersByFirstNameOrLastName() {
        var page = new PageImpl<>(List.of(member));
        var pageable = Pageable.unpaged();
        when(memberRepository.findAllByFirstNameAndLastName(member.getFirstName(), member.getLastName(), pageable)).thenReturn(page);

        var memberDtoPage = memberService.getMembersByFirstNameAndLastName(member.getFirstName(), member.getLastName(), pageable);

        assertThat(memberDtoPage).containsExactly(MEMBER_MAPPER.toTarget(member));
    }

    @Test
    void getMembersByEntityStatus() {
        var page = new PageImpl<>(List.of(member));
        when(memberRepository.findAllByEntityStatus(EntityStatus.ACTIVE, Pageable.unpaged())).thenReturn(page);

        var memberDtoPage = memberService.getMembersByEntityStatus(EntityStatus.ACTIVE, Pageable.unpaged());

        assertThat(memberDtoPage).containsExactly(MEMBER_MAPPER.toTarget(member));
    }

    @Test
    void getMembersByName() {
        var page = new PageImpl<>(List.of(member));
        when(memberRepository.findAllByValue(member.getLastName(), Pageable.unpaged())).thenReturn(page);

        var memberDtoPage = memberService.getMembersByName(member.getLastName(), Pageable.unpaged());

        assertThat(memberDtoPage).containsExactly(MEMBER_MAPPER.toTarget(member));
    }

    @Test
    void getMembersEmailByIds() {
        when(memberRepository.findAllByIdInAndEntityStatus(Collections.singleton(member.getId()), EntityStatus.ACTIVE)).thenReturn(Set.of(member));

        assertThat(memberService.getMembersEmailByIds(Set.of(member.getId()))).containsExactly(member.getEmail());
    }

    @Test
    void deactivateMember() {
        when(memberRepository.findByIdAndEntityStatusCustom(member.getId(), EntityStatus.ACTIVE)).thenReturn(member);
        when(memberRepository.save(member)).thenAnswer(i -> i.getArgument(0));

        assertSame(EntityStatus.INACTIVE, memberService.deactivateMember(member.getId()).getEntityStatus());

        verify(dogHasHandlerService).deactivateDogHasHandlersByMemberId(member.getId());
        verify(baseParticipantObserver).deactivateParticipantsByMemberOrHandlerId(member.getId());
        verify(baseEventObserver).deactivateBaseEventsByMemberId(member.getId());
    }

    @Test
    void activateMember() {
        when(memberRepository.findByIdAndEntityStatusCustom(member.getId(), EntityStatus.INACTIVE)).thenReturn(member);
        when(memberRepository.save(member)).thenAnswer(i -> i.getArgument(0));
        memberService.activateMember(member.getId());

        verify(member).setEntityStatus(EntityStatus.ACTIVE);
        verify(dogHasHandlerService).activateDogHasHandlersByMemberId(member.getId());
        verify(baseParticipantObserver).activateParticipantsByMemberOrHandlerId(member.getId());
        verify(baseEventObserver).activateBaseEventsByMemberId(member.getId());
    }

    @Test
    void migrateDataToV2() {
        var membershipFee = TestFixture.createMembershipFee();
        var card = mock(Card.class);
        member.setMembershipFees(Set.of(membershipFee));
        when(memberRepository.findAll()).thenReturn(List.of(member));
        when(cardRepository.findAllByMemberId(member.getId())).thenReturn(Set.of(card));
        when(card.getCode()).thenReturn(UUID.randomUUID().toString());

        memberService.migrateDataToV2();

        verify(memberRepository).save(memberCaptor.capture());

        var memberV2 = memberCaptor.getValue();
        assertEquals(member.getId(), memberV2.getId());
        assertThat(memberV2.getMembershipFees())
                .singleElement()
                .satisfies(mfv2 -> assertEquals(membershipFee.getPaidDate(), mfv2.getPaidDate()));
        assertThat(memberV2.getCards())
                .singleElement()
                .satisfies(cardV2 -> assertEquals(card.getCode(), cardV2.getCode()));
    }

    @Nested
    class GetMemberById {
        @BeforeEach
        void setUp() {
            when(memberRepository.findByIdAndEntityStatusCustom(member.getId(), EntityStatus.ACTIVE)).thenReturn(member);
        }

        @Test
        void memberIsCorrectlyMapped() {
            var memberDto = memberService.getMemberById(member.getId());

            assertThat(memberDto)
                    .usingRecursiveAssertion()
                    .isEqualTo(MEMBER_MAPPER.toTarget(member));
        }

        @Test
        void dogHasHandlerIsCorrectlyMapped() {
            var dogHasHandlerReference = mock(DogHasHandlerReference.class, Answers.RETURNS_DEEP_STUBS);
            when(dogHasHandlerReference.getId()).thenReturn(UUID.randomUUID());
            when(dogHasHandlerReference.getDog().getId()).thenReturn(UUID.randomUUID());
            when(dogHasHandlerReference.getDog().getName()).thenReturn("Bonsai");
            when(dogHasHandlerReferenceRepository.findAllByMemberId(member.getId())).thenReturn(Set.of(dogHasHandlerReference));

            var memberDto = memberService.getMemberById(member.getId());

            assertThat(memberDto.getDogHasHandlerSet())
                    .singleElement()
                    .usingRecursiveAssertion()
                    .isEqualTo(MEMBER_MAPPER.toDogHasHandlerDto(dogHasHandlerReference));
        }
    }

    @Nested
    class SetActiveMemberToBaseEvent {

        private Event event;

        @BeforeEach
        void setUp() {
            event = spy(TestFixture.createEvent());
        }

        @Test
        void memberIsActive() {
            event.setMember(null);
            event.setMemberId(member.getId());
            when(memberRepository.findOneByIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            memberService.setActiveMemberToBaseEvent(event);

            assertSame(member, event.getMember());

        }

        @Test
        void memberIsInactive() {
            event.setMember(null);
            when(memberRepository.findOneByIdAndEntityStatus(event.getMemberId(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            memberService.setActiveMemberToBaseEvent(event);

            assertNull(event.getMember());
        }

        @Test
        void noNeedToSetMember() {
            memberService.setActiveMemberToBaseEvent(event);

            verifyNoInteractions(memberRepository);

            assertNotNull(event.getMember());
        }
    }

    @Nested
    class SaveMember {
        @Test
        void emailDoesNotExists() {
            when(memberRepository.setMetadataAndSave(argThat(m -> member.getId() == m.getId()))).thenAnswer(i -> i.getArgument(0));

            memberService.saveMember(MEMBER_MAPPER.toTarget(member));

            verify(memberRepository).setMetadataAndSave(memberCaptor.capture());
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
            when(memberRepository.setMetadataAndSave(argThat(m -> member.getId() == m.getId()))).thenAnswer(i -> i.getArgument(0));

            memberService.saveMember(MEMBER_MAPPER.toTarget(member));

            verify(memberRepository).setMetadataAndSave(memberCaptor.capture());
            assertThat(memberCaptor.getValue())
                    .usingRecursiveComparison()
                    .isEqualTo(member);
        }
    }

    @Nested
    class DeleteMemberById {
        @Test
        void memberDoesNotExist() {
            var memberId = UUID.randomUUID();
            when(memberRepository.findByIdAndEntityStatusCustom(memberId, EntityStatus.ACTIVE)).thenThrow(new NoSuchElementException());

            assertThrows(NoSuchElementException.class, () -> memberService.deleteMemberById(memberId));

            verifyNoInteractions(dogHasHandlerService, baseParticipantObserver, baseEventObserver);

        }

        @Test
        void memberExist() {
            when(memberRepository.findByIdAndEntityStatusCustom(member.getId(), EntityStatus.ACTIVE)).thenReturn(member);

            memberService.deleteMemberById(member.getId());

            verify(dogHasHandlerService).deleteDogHasHandlersByMemberId(member.getId());
            verify(baseParticipantObserver).deleteParticipantsByMemberOrHandlerId(member.getId());
            verify(baseEventObserver).deleteBaseEventsByMemberId(member.getId());
            verify(member).setEntityStatus(EntityStatus.DELETED);
            verify(member).setEmail("%s---%s".formatted(member.getEmail(), member.getId()));
        }
    }
}
