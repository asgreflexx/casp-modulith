package casp.web.backend.member;


import casp.web.backend.calendar.BaseEventObserver;
import casp.web.backend.calendar.CourseService;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.deprecated.member.CardRepository;
import casp.web.backend.deprecated.member.MemberOldRepository;
import casp.web.backend.dog.DogHasHandlerService;
import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    private final DogHasHandlerService dogHasHandlerService;
    private final BaseEventObserver baseEventObserver;
    private final CourseService courseService;
    private final CardRepository cardRepository;
    private final MemberOldRepository memberOldRepository;

    @Autowired
    MemberServiceImpl(MemberRepository memberRepository,
                      DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository, DogHasHandlerService dogHasHandlerService,
                      BaseEventObserver baseEventObserver,
                      CourseService courseService,
                      CardRepository cardRepository,
                      MemberOldRepository memberOldRepository) {
        this.memberRepository = memberRepository;
        this.dogHasHandlerService = dogHasHandlerService;
        this.baseEventObserver = baseEventObserver;
        this.courseService = courseService;
        this.cardRepository = cardRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
        this.memberOldRepository = memberOldRepository;
    }

    @Override
    public Page<MemberDto> getMembersByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        var memberPage = memberRepository.findAllByFirstNameAndLastName(firstName, lastName, pageable);
        return MEMBER_MAPPER.toTargetPage(memberPage);
    }

    @Override
    public Page<MemberDto> getMembersByEntityStatus(EntityStatus entityStatus, Pageable pageable) {
        var memberPage = memberRepository.findAllByEntityStatus(entityStatus, pageable);
        return MEMBER_MAPPER.toTargetPage(memberPage);
    }

    @Override
    public MemberDto getMemberById(UUID id) {
        return mapToMemberDto(memberRepository.findByIdAndEntityStatusCustom(id, EntityStatus.ACTIVE));
    }

    @Override
    public MemberDto saveMember(MemberDto memberDto) {
        var member = MEMBER_MAPPER.toSource(memberDto);

        verifyForMemberConflict(memberDto, member);

        return mapToMemberDto(memberRepository.save(member));
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
    public MemberDto deactivateMember(UUID id) {
        var member = memberRepository.findByIdAndEntityStatusCustom(id, EntityStatus.ACTIVE);
        dogHasHandlerService.deactivateDogHasHandlersByMemberId(id);
        baseEventObserver.deactivateBaseEventsByMemberId(id);
        member.setEntityStatus(EntityStatus.INACTIVE);
        return MEMBER_MAPPER.toTarget(memberRepository.save(member));
    }

    @Override
    public MemberDto activateMember(UUID id) {
        var member = memberRepository.findByIdAndEntityStatusCustom(id, EntityStatus.INACTIVE);
        dogHasHandlerService.activateDogHasHandlersByMemberId(id);
        baseEventObserver.activateBaseEventsByMemberId(id);
        member.setEntityStatus(EntityStatus.ACTIVE);
        return mapToMemberDto(memberRepository.save(member));
    }

    @Override
    public Page<MemberDto> getMembersByName(String name, Pageable pageable) {
        var memberPage = memberRepository.findAllByValue(name, pageable);
        return MEMBER_MAPPER.toTargetPage(memberPage);
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

    private void verifyForMemberConflict(MemberDto memberDto, Member member) {
        memberRepository.findOneByEmail(memberDto.getEmail())
                .ifPresent(m -> {
                    if (!member.equals(m)) {
                        var msg = "Member with email %s already exists.".formatted(member.getEmail());
                        LOG.error(msg);
                        throw new IllegalStateException(msg);
                    }
                });
    }

    private MemberDto mapToMemberDto(Member member) {
        var memberDto = MEMBER_MAPPER.toTarget(member);
        var dogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByMemberId(member.getId());
        memberDto.setDogHasHandlerSet(MEMBER_MAPPER.toDogHasHandlerDtoSet(dogHasHandlerSet));
        memberDto.setSpaces(courseService.getSpacesByDogHasHandlers(dogHasHandlerSet));
        return memberDto;
    }
}
