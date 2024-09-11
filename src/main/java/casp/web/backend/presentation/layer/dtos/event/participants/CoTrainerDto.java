package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.documents.event.participants.CoTrainer;
import casp.web.backend.presentation.layer.dtos.member.MemberDto;

public class CoTrainerDto extends CoTrainer {
    private MemberDto member;

    public MemberDto getMember() {
        return member;
    }

    public void setMember(final MemberDto member) {
        this.member = member;
    }
}
