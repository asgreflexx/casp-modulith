package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
    public void replaceParticipants(final Event event, final Set<UUID> participantsId) {
        var eventParticipantSet = createEventParticipants(event, participantsId);
        replaceParticipantsAndSetMetadata(event, eventParticipantSet);
    }

    @Override
    public Set<EventParticipant> getActiveParticipantsIfMembersOrDogHasHandlerAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(p -> findMember(p.getMemberOrHandlerId())
                        .map(m -> {
                            p.setMember(m);
                            return p;
                        })
                        .stream())
                .collect(Collectors.toSet());
    }

    private Set<EventParticipant> createEventParticipants(final Event event, final Set<UUID> participantsId) {
        return participantsId
                .stream()
                .flatMap(id -> findMember(id)
                        .map(m -> new EventParticipant(event, m)).stream())
                .collect(Collectors.toSet());
    }

    private Optional<Member> findMember(final UUID memberId) {
        return memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE);
    }
}
