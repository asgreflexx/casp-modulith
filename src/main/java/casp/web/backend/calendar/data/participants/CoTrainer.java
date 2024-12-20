package casp.web.backend.calendar.data.participants;

import casp.web.backend.common.reference.MemberReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.UUID;

public class CoTrainer extends BaseParticipant {
    @Valid
    @NotNull
    @DBRef
    private MemberReference member;

    public CoTrainer() {
        super(BaseParticipantType.CO_TRAINER);
    }

    public CoTrainer(MemberReference member) {
        this();
        this.member = member;
    }

    public MemberReference getMember() {
        return member;
    }

    public void setMember(MemberReference member) {
        this.member = member;
    }

    @Override
    public UUID getId() {
        return member.getId();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
