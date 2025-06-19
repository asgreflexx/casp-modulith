package casp.web.backend.member;


import casp.web.backend.calendar.BaseEventObserver;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.deprecated.member.CardRepository;
import casp.web.backend.deprecated.member.MemberOldRepository;
import casp.web.backend.dog.DogHasHandlerService;
import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import casp.web.backend.member.data.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.member.MemberV2Mapper.MEMBER_V2_MAPPER;
import static casp.web.backend.member.MemberMapper.MEMBER_MAPPER;

@Service
class MemberServiceImpl implements MemberService {
    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);
    private static final String EMAIL_FORMAT_IF_DELETED = "%s---%s";

    private final MemberRepository memberRepository;
    private final DogHasHandlerService dogHasHandlerService;
    private final BaseEventObserver baseEventObserver;
    private final CardRepository cardRepository;
    private final MemberOldRepository memberOldRepository;

    @Autowired
    MemberServiceImpl(MemberRepository memberRepository,
                      DogHasHandlerService dogHasHandlerService,
                      BaseEventObserver baseEventObserver,
                      CardRepository cardRepository,
                      MemberOldRepository memberOldRepository) {
        this.memberRepository = memberRepository;
        this.dogHasHandlerService = dogHasHandlerService;
        this.baseEventObserver = baseEventObserver;
        this.cardRepository = cardRepository;
        this.memberOldRepository = memberOldRepository;
    }

    @Override
    public Page<MemberDto> getMembersByEntityStatusNameAndRoles(EntityStatus entityStatus, String name, final Set<Role> roles, Pageable pageable) {
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
        member.setEmail(EMAIL_FORMAT_IF_DELETED.formatted(member.getEmail(), id));
        member.setEntityStatus(EntityStatus.DELETED);
        memberRepository.save(member);
    }

    @Override
    public Set<String> getMembersEmailByIds(Set<UUID> membersId) {
        return memberRepository.findAllByIdInAndEntityStatus(membersId, EntityStatus.ACTIVE)
                .stream()
                .map(Member::getEmail)
                .collect(Collectors.toSet());
    }

    @Override
    public void migrateDataToV2() {
        memberOldRepository.findAll().forEach(mv1 -> {
            var cardV1Set = cardRepository.findAllByMemberId(mv1.getId());
            var memberV2 = MEMBER_V2_MAPPER.toMemberV2(mv1);
            memberV2.setCards(MEMBER_V2_MAPPER.toCardV2Set(cardV1Set));
            memberRepository.save(memberV2);
        });
    }

    @Override
    public Set<String> getActiveMembersEmail() {
        return memberRepository.findAllActiveMembersEmails();
    }

    @Override
    public Page<MemberDto> getMembersByNotDogId(UUID dogId, String name, Pageable pageable) {
        return MEMBER_MAPPER.toTargetPage(memberRepository.findAllByNotDogId(dogId, name, pageable));
    }

    @Override
    public MemberDto toggleStatus(final UUID id) {
        var member = getMemberIfNotDeleted(id);
        if (member.getEntityStatus() == EntityStatus.ACTIVE) {
            deactivateMember(member);
        } else {
            activateMember(member);
        }
        return MEMBER_MAPPER.toTarget(memberRepository.save(member));
    }

    // it fails if the member isn't active or another member with the same email already exists
    private void analyseMember(Member member) {
        memberRepository.findByIdAndEntityStatusCustom(member.getId(), EntityStatus.ACTIVE);

        memberRepository.findOneByEmail(member.getEmail())
                .ifPresent(m -> {
                    if (!member.equals(m)) {
                        var msg = "Member with email %s already exists.".formatted(member.getEmail());
                        LOG.error(msg);
                        throw new IllegalStateException(msg);
                    }
                });
    }

    private Member getMemberIfNotDeleted(final UUID id) {
        return memberRepository.findOneByIdAndEntityStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> {
                    var msg = "Member with id %s not found.".formatted(id);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
    }

    private void deactivateMember(final Member member) {
        dogHasHandlerService.deactivateDogHasHandlersByMemberId(member.getId());
        baseEventObserver.deactivateBaseEventsByMemberId(member.getId());
        member.setEntityStatus(EntityStatus.INACTIVE);
    }

    private void activateMember(final Member member) {
        dogHasHandlerService.activateDogHasHandlersByMemberId(member.getId());
        baseEventObserver.activateBaseEventsByMemberId(member.getId());
        member.setEntityStatus(EntityStatus.ACTIVE);
    }
}
