package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.presentation.layer.dtos.member.SimpleMemberDto;

public class CoTrainerDto extends BaseParticipantDto {
    private SimpleMemberDto member;

    public CoTrainerDto() {
        super(CoTrainer.PARTICIPANT_TYPE);
    }

    public SimpleMemberDto getMember() {
        return member;
    }

    public void setMember(final SimpleMemberDto member) {
        this.member = member;
    }
}
