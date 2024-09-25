package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.presentation.layer.dtos.member.SimpleMemberDto;

public class EventParticipantDto extends BaseParticipantDto {
    private SimpleMemberDto member;

    public EventParticipantDto() {
        super(EventParticipant.PARTICIPANT_TYPE);
    }

    public SimpleMemberDto getMember() {
        return member;
    }

    public void setMember(final SimpleMemberDto member) {
        this.member = member;
    }
}
