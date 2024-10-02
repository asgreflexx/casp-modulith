package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
class CoTrainerServiceImpl extends BaseParticipantServiceImpl<CoTrainer, Course> implements CoTrainerService {
    private final MemberRepository memberRepository;

    @Autowired
    CoTrainerServiceImpl(final BaseParticipantRepository baseParticipantRepository, final MemberRepository memberRepository) {
        super(baseParticipantRepository, CoTrainer.PARTICIPANT_TYPE);
        this.memberRepository = memberRepository;
    }

    @Override
    public void replaceParticipants(final Course course, final Set<UUID> coTrainersId) {
        var coTrainerSet = createCoTrainers(course, coTrainersId);
        baseParticipantRepository.deleteAllByBaseEventIdAndParticipantType(course.getId(), participantType);
        baseParticipantRepository.saveAll(coTrainerSet);
    }

    @Override
    public Set<CoTrainer> getActiveParticipantsIfMembersOrDogHasHandlerAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(ct -> findMember(ct.getMemberOrHandlerId())
                        .map(m -> {
                            ct.setMember(m);
                            return ct;
                        })
                        .stream())
                .collect(Collectors.toSet());
    }

    private Set<CoTrainer> createCoTrainers(final Course course, final Set<UUID> participantsId) {
        return participantsId
                .stream()
                .flatMap(id -> findMember(id).map(m -> new CoTrainer(course, m)).stream())
                .collect(Collectors.toSet());
    }

    private Optional<Member> findMember(final UUID memberId) {
        return memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE);
    }
}
