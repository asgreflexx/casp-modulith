package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Set<ParticipantMember> getActiveCoTrainersIfMembersAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(ct -> memberRepository.findByIdAndEntityStatus(ct.getMemberOrHandlerId(), EntityStatus.ACTIVE)
                        .map(m -> new ParticipantMember(ct, m))
                        .stream())
                .collect(Collectors.toSet());
    }
}
