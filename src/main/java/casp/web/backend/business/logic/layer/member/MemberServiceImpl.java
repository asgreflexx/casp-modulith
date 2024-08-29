package casp.web.backend.business.logic.layer.member;


import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.Role;
import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Member Service
 *
 * @author iris_e
 */

@Service
class MemberServiceImpl implements MemberService {
    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);
    private static final String EMAIL_FORMAT_IF_DELETED = "%s---%s";

    private final MemberRepository memberRepository;
    private final DogHasHandlerService dogHasHandlerService;

    @Autowired
    MemberServiceImpl(final MemberRepository memberRepository, final DogHasHandlerService dogHasHandlerService) {
        this.memberRepository = memberRepository;
        this.dogHasHandlerService = dogHasHandlerService;
    }

    @Override
    public List<Member> getMembersByFirstNameAndLastName(final String firstName, final String lastName) {
        return memberRepository.findAllByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public List<Role> getAllAvailableRoles() {
        return Role.getAllRolesSorted();
    }

    @Override
    public Page<Member> getMembersByEntityStatus(final EntityStatus entityStatus, final Pageable pageable) {
        return memberRepository.findAllByEntityStatus(entityStatus, pageable);
    }

    @Override
    public Member getMemberById(final UUID id) {
        return findMember(id, EntityStatus.ACTIVE);
    }

    @Transactional
    @Override
    public Member saveMember(final Member member) {
        memberRepository.findMemberByEmail(member.getEmail())
                .ifPresent(m -> {
                    if (!member.equals(m)) {
                        var msg = "Member with email %s already exists.".formatted(m.getEmail());
                        LOG.error(msg);
                        throw new IllegalStateException(msg);
                    }
                });
        return memberRepository.save(member);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMemberById(final UUID id) {
        memberRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED).ifPresent(member -> {
            dogHasHandlerService.deleteDogHasHandlersByMemberId(id);
            // TODO: delete Card
            // TODO: delete BaseEvents
            // TODO: delete BaseParticipants
            member.setEmail(EMAIL_FORMAT_IF_DELETED.formatted(member.getEmail(), id));
            member.setEntityStatus(EntityStatus.DELETED);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Member deactivateMember(final UUID id) {
        var member = getMemberById(id);
        dogHasHandlerService.deactivateDogHasHandlersByMemberId(id);
        // TODO: deactivate Card
        // TODO: deactivate BaseEvents
        // TODO: deactivate BaseParticipants
        member.setEntityStatus(EntityStatus.INACTIVE);
        return member;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Member activateMember(final UUID id) {
        var member = findMember(id, EntityStatus.INACTIVE);
        dogHasHandlerService.activateDogHasHandlersByMemberId(id);
        // TODO: activate Card
        // TODO: activate BaseEvents
        // TODO: activate BaseParticipants
        member.setEntityStatus(EntityStatus.ACTIVE);
        return member;
    }

    @Override
    public Page<Member> getMembersByName(final String name, final Pageable pageable) {
        return memberRepository.findAllByValue(name, pageable);
    }

    @Override
    public Set<String> getMembersEmailByIds(final Set<UUID> membersId) {
        return memberRepository.findAllByIdInAndEntityStatus(membersId, EntityStatus.ACTIVE)
                .stream()
                .map(Member::getEmail)
                .collect(Collectors.toSet());
    }

    private Member findMember(final UUID id, final EntityStatus entityStatus) {
        return memberRepository.findByIdAndEntityStatus(id, entityStatus).orElseThrow(() -> {
            var msg = "Member with id %s not found or it isn't %s.".formatted(id, entityStatus);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }
}
