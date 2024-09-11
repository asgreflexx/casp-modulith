package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
class EventParticipantServiceImpl extends BaseParticipantServiceImpl<EventParticipant, Event> implements EventParticipantService {
    private final MemberRepository memberRepository;

    @Autowired
    EventParticipantServiceImpl(final BaseParticipantRepository baseParticipantRepository, final MemberRepository memberRepository) {
        super(baseParticipantRepository, EventParticipant.PARTICIPANT_TYPE);
        this.memberRepository = memberRepository;
    }

    @Override
    public Set<ParticipantMember> getActiveEventParticipantsIfMembersAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(p -> memberRepository.findByIdAndEntityStatus(p.getMemberOrHandlerId(), EntityStatus.ACTIVE)
                        .map(m -> new ParticipantMember(p, m))
                        .stream())
                .collect(Collectors.toSet());
    }
}
