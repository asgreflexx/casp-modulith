package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.member.Member;

public record ParticipantMember(BaseParticipant participant, Member member) {
}
