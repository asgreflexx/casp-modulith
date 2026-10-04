package casp.web.backend.member;

import casp.web.backend.calendar.BaseEventObserver;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.exception.MemberEMailConflictException;
import casp.web.backend.common.exception.MemberStateConflictException;
import casp.web.backend.dog.DogHasHandlerService;
import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import casp.web.backend.member.data.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.member.MemberMapper.MEMBER_MAPPER;

@RequiredArgsConstructor
@Slf4j
@Service
class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final DogHasHandlerService dogHasHandlerService;
    private final BaseEventObserver baseEventObserver;

    @Override
    public Page<MemberDto> getMembersByEntityStatusNameAndRoles(EntityStatus entityStatus, String name, Set<Role> roles, Pageable pageable) {
        var memberPage = memberRepository.findAllByEntityStatusNameAndRoles(entityStatus, name, roles, pageable);
        return MEMBER_MAPPER.toTargetPage(memberPage);
    }

    @Override
    public MemberDto getMemberById(UUID id) {
        var member = getMemberIfNotDeleted(id);
        return MEMBER_MAPPER.toTarget(member);
    }

    @Override
    public MemberDto saveMember(MemberDto memberDto) {
        var member = MEMBER_MAPPER.toSource(memberDto);

        analyseMember(member);

        return MEMBER_MAPPER.toTarget(memberRepository.save(member));
    }

    @Override
    public void deleteMemberById(UUID id) {
        var member = memberRepository.findByIdAndEntityStatusCustom(id, EntityStatus.ACTIVE);
        dogHasHandlerService.deleteDogHasHandlersByMemberId(id);
        baseEventObserver.deleteBaseEventsByMemberId(id);
        member.setEntityStatus(EntityStatus.DELETED);
        memberRepository.save(member);
    }

    @Override
    public Set<String> getMembersEmailByIds(Set<UUID> membersId) {
        return memberRepository.findAllByIdInAndEntityStatus(membersId, EntityStatus.ACTIVE).stream().map(Member::getEmail).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getActiveMembersEmail() {
        return memberRepository.findAllActiveMembersEmails();
    }

    @Override
    public MemberDto toggleStatus(UUID id) {
        var member = getMemberIfNotDeleted(id);
        if (member.getEntityStatus() == EntityStatus.ACTIVE) {
            deactivateMember(member);
        } else {
            activateMember(member);
        }
        return MEMBER_MAPPER.toTarget(memberRepository.save(member));
    }

    @Override
    public MembershipFeesStatsDto getMembershipFeesStats() {
        return memberRepository.getMembershipFeesStats();
    }

    // if member exists, it must be active
    // if an existing member already contains the member.email, it will fail
    private void analyseMember(Member member) {
        memberRepository.findById(member.getId()).ifPresent(m -> {
            if (m.getEntityStatus() != EntityStatus.ACTIVE) {
                var msg = "Member with id %s is not active.".formatted(member.getId());
                log.error(msg);
                throw new MemberStateConflictException(msg);
            }
        });
        memberRepository.findOneByEmailAndEntityStatusIsNot(member.getEmail(), EntityStatus.DELETED).ifPresent(m -> {
            if (!member.equals(m)) {
                var msg = "Member with email %s already exists.".formatted(member.getEmail());
                log.error(msg);
                throw new MemberEMailConflictException(msg);
            }
        });
    }

    private Member getMemberIfNotDeleted(UUID id) {
        return memberRepository.findOneByIdAndEntityStatusNot(id, EntityStatus.DELETED).orElseThrow(() -> {
            var msg = "Member with id %s not found.".formatted(id);
            log.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    private void deactivateMember(Member member) {
        dogHasHandlerService.deactivateDogHasHandlersByMemberId(member.getId());
        baseEventObserver.deactivateBaseEventsByMemberId(member.getId());
        member.setEntityStatus(EntityStatus.INACTIVE);
    }

    private void activateMember(Member member) {
        dogHasHandlerService.activateDogHasHandlersByMemberId(member.getId());
        baseEventObserver.activateBaseEventsByMemberId(member.getId());
        member.setEntityStatus(EntityStatus.ACTIVE);
    }
}
