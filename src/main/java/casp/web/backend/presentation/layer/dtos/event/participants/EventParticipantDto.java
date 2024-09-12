package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.presentation.layer.dtos.member.MemberDto;

public class EventParticipantDto extends EventParticipant {
    private MemberDto member;

    public MemberDto getMember() {
        return member;
    }

    public void setMember(final MemberDto member) {
        this.member = member;
    }
}
